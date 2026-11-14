package yass

import spock.lang.Specification
import yass.input.*

import javax.swing.*
import java.awt.Component
import java.awt.DefaultKeyboardFocusManager
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

class ShiftArrowSelectionSpec extends Specification {
    def 'two arrow taps select a word when acceleration is enabled and two notes otherwise'() {
        given:
        def table = selectionTable(trailingSpaces, preferredTrailingSpaces)
        int initialRow = up ? 8 : 0
        table.setRowSelectionInterval(initialRow, initialRow)
        def previousFocusManager = KeyboardFocusManager.currentKeyboardFocusManager
        KeyboardFocusManager.setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager() {
            @Override
            Component getFocusOwner() { table }
        })
        def registry = new EditorKeyBindingRegistry()
        def commands = []
        int key = up ? KeyEvent.VK_UP : KeyEvent.VK_DOWN
        def stroke = KeyStroke.getKeyStroke(key, KeyEvent.SHIFT_DOWN_MASK)
        registry.bind(stroke, new SimpleEditorCommand('note', { true }, {
            commands << 'note'
            if (up) table.selectPrevBeat() else table.selectNextBeat()
        }))
        registry.bind(stroke, 2, new SimpleEditorCommand('word', { true }, {
            commands << 'word'
            if (up) table.extendSelectionUpToWordBoundary() else table.extendSelectionDownToWordBoundary()
        }))
        registry.bind(stroke, 3, new SimpleEditorCommand('page', { true }, {
            commands << 'page'
            if (up) table.extendSelectionUpToPageBreak() else table.extendSelectionDownToPageBreak()
        }))
        def dispatcher = new EditorKeyDispatcher({ true }, {
            new EditorInputContext(focusArea, false, false, false, false, false, true, false)
        }, registry, new KeySequenceTracker(10000), { enabled })

        when:
        2.times {
            assert dispatcher.dispatchKeyEvent(new KeyEvent(table, KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), KeyEvent.SHIFT_DOWN_MASK, key, KeyEvent.CHAR_UNDEFINED))
            dispatcher.dispatchKeyEvent(new KeyEvent(table, KeyEvent.KEY_RELEASED,
                    System.currentTimeMillis(), 0, key, KeyEvent.CHAR_UNDEFINED))
        }

        then:
        table.selectedRows == (up ? (6..8) : (enabled ? (0..5) : (0..2))) as int[]
        commands == (enabled ? ['note', 'word'] : ['note', 'note'])

        when:
        assert dispatcher.dispatchKeyEvent(new KeyEvent(table, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), KeyEvent.SHIFT_DOWN_MASK, key, KeyEvent.CHAR_UNDEFINED))

        then:
        commands == (enabled ? ['note', 'word', 'page'] : ['note', 'note', 'note'])
        table.selectedRows == (enabled ? (up ? (0..8) : (0..9)) : (up ? (5..8) : (0..3))) as int[]

        cleanup:
        KeyboardFocusManager.setCurrentKeyboardFocusManager(previousFocusManager)

        where:
        [up, enabled, trailingSpaces, preferredTrailingSpaces, focusArea] << [[false, true], [false, true],
                [false, true], [false, true], [FocusArea.SHEET, FocusArea.LYRICS_VIEW]].combinations()
    }

    def 'repeated upward selection grows to the first note and changing direction preserves selected notes'() {
        given:
        def table = selectionTable(true)
        table.setRowSelectionInterval(2, 2)

        when:
        3.times { table.selectPrevBeat() }

        then:
        table.selectedRows == (0..2) as int[]

        when:
        table.selectNextBeat()

        then:
        table.selectedRows == (0..3) as int[]
    }

    def 'word extension reaches the boundary from any syllable and advances when already there'() {
        given:
        def table = selectionTable(trailingSpaces)
        table.setRowSelectionInterval(initialRow, initialRow)

        when:
        if (up) table.extendSelectionUpToWordBoundary() else table.extendSelectionDownToWordBoundary()

        then:
        table.selectedRows == (Math.min(initialRow, boundary)..Math.max(initialRow, boundary)) as int[]

        where:
        up    | initialRow | boundary | trailingSpaces
        false | 0          | 5        | true
        false | 2          | 5        | true
        false | 5          | 8        | true
        false | 8          | 14       | true
        true  | 5          | 0        | true
        true  | 3          | 0        | true
        true  | 6          | 0        | true
        true  | 10         | 6        | true
        false | 0          | 5        | false
        false | 2          | 5        | false
        false | 5          | 8        | false
        false | 8          | 14       | false
        true  | 5          | 0        | false
        true  | 3          | 0        | false
        true  | 6          | 0        | false
        true  | 10         | 6        | false
    }

    def 'word extension stops at the page edge even without a separating space'() {
        given:
        def table = selectionTable(trailingSpaces)
        table.getRowAt(8).setText('word')
        table.setRowSelectionInterval(up ? 14 : 6, up ? 14 : 6)

        when:
        if (up) table.extendSelectionUpToWordBoundary() else table.extendSelectionDownToWordBoundary()

        then:
        table.selectedRows == (up ? (10..14) : (6..8)) as int[]

        where:
        [up, trailingSpaces] << [[false, true], [false, true]].combinations()
    }

    def 'word extension uses the requested edge regardless of how the range was selected'() {
        given:
        def table = selectionTable(trailingSpaces)
        table.setRowSelectionInterval(up ? 4 : 8, up ? 5 : 7)

        when:
        if (up) table.extendSelectionUpToWordBoundary() else table.extendSelectionDownToWordBoundary()

        then:
        table.selectedRows == (up ? (0..5) : (7..14)) as int[]

        where:
        [up, trailingSpaces] << [[false, true], [false, true]].combinations()
    }

    def 'single-note extension skips page breaks in either direction'() {
        given:
        def table = selectionTable(true)
        table.setRowSelectionInterval(up ? 10 : 8, up ? 10 : 8)

        when:
        if (up) table.selectPrevBeat() else table.selectNextBeat()

        then:
        table.selectedRows == (8..10) as int[]
        table.getRowAt(table.selectionModel.leadSelectionIndex).isNote()

        when:
        if (up) table.selectPrevBeat() else table.selectNextBeat()

        then:
        table.selectedRows == (up ? (7..10) : (8..11)) as int[]

        where:
        up << [false, true]
    }

    def 'extending at the song limits leaves selection intact'() {
        given:
        def table = selectionTable(true)
        table.setRowSelectionInterval(up ? 0 : 14, up ? 0 : 14)
        def original = table.selectedRows

        when:
        if (up) {
            table.selectPrevBeat()
            table.extendSelectionUpToWordBoundary()
        } else {
            table.selectNextBeat()
            table.extendSelectionDownToWordBoundary()
        }

        then:
        table.selectedRows == original

        where:
        up << [false, true]
    }

    private YassTable selectionTable(boolean trailingSpaces, boolean preferredTrailingSpaces = trailingSpaces) {
        I18.setDefaultLanguage()
        def model = new YassTableModel()
        def texts = trailingSpaces
                ? ['su', 'per', 'ca', 'li', 'fra', 'gile ', 'next', '~', 'word ']
                : ['su', 'per', 'ca', 'li', 'fra', 'gile', ' next', '~', 'word']
        texts.eachWithIndex { text, i -> model.addRow(note(i, text)) }
        model.addRow(new YassRow('-', '36', '', '', ''))
        ['a', 'no', 'ther', '~', trailingSpaces ? 'line ' : 'line'].eachWithIndex { text, i ->
            model.addRow(note(i + 10, text))
        }
        model.addRow(new YassRow('E', '', '', '', ''))
        def table = new YassTable(model, Stub(YassProperties) {
            isUncommonSpacingAfter() >> preferredTrailingSpaces
        })
        table.setModel(model)
        table
    }

    private static YassRow note(int index, String text) {
        new YassRow(':', (index * 4) as String, '4', '10', text.replace(' ' as char, YassRow.SPACE))
    }
}
