package yass

import spock.lang.Specification
import yass.autocorrect.YassAutoCorrect

import javax.swing.*
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

class EditorNoteDeletionSpec extends Specification {
    private DeletionActions actions

    def setup() {
        I18.setDefaultLanguage()
        def props = Stub(YassProperties)
        actions = new DeletionActions(new YassSheet(),
                Stub(YassPlayer, constructorArgs: [null, props]), new YassLyrics(props))
    }

    def cleanup() {
        if (actions != null) {
            def field = YassActions.getDeclaredField('editorKeyDispatcher')
            field.accessible = true
            KeyboardFocusManager.currentKeyboardFocusManager.removeKeyEventDispatcher(field.get(actions))
        }
    }

    def 'Delete erases selected syllables and undo restores the complete song'() {
        given:
        YassTable table = song()
        selected.each { table.addRowSelectionInterval(it, it) }
        table.addUndo()
        String original = table.toString()

        when:
        deleteAction(table, 0).actionPerformed(null)

        then:
        notes(table)*.text == remaining.collect { it.replace(' ' as char, YassRow.SPACE) }
        notes(table)*.beatInt == remainingBeats
        notes(table).every { it.lengthInt == 4 && it.heightInt == 10 && it.type == ':' }
        table.getRowAt(0).toString() == '#TITLE:Deletion'
        table.getRowAt(table.rowCount - 1).isEnd()
        table.selectedRows.every { table.getRowAt(it).isNote() }
        notes(table) || table.selectedRow == -1

        when:
        table.undoRows()

        then:
        table.toString() == original

        when:
        table.redoRows()

        then:
        notes(table)*.beatInt == remainingBeats

        where:
        selected        | remaining               | remainingBeats
        [2]             | ['hel', 'world ', 'end '] | [0, 8, 16]
        [1, 2]          | ['world ', 'end ']        | [8, 16]
        [2, 5]          | ['hel', 'world ']         | [0, 8]
        [1, 2, 3, 4, 5] | []                      | []
    }

    def 'deleting the last note removes trailing page breaks and selects a surviving note'() {
        given:
        YassTable table = song()
        table.setRowSelectionInterval(5, 5)

        when:
        deleteAction(table, 0).actionPerformed(null)

        then:
        table.rowCount == 5
        table.getRowAt(4).isEnd()
        table.selectedRows == [3] as int[]
        notes(table)*.beatInt == [0, 4, 8]
    }

    def 'Ctrl Delete removes the note while keeping its syllable on its neighbor'() {
        given:
        YassTable table = song()
        table.setRowSelectionInterval(1, 1)

        when:
        deleteAction(table, KeyEvent.CTRL_DOWN_MASK).actionPerformed(null)

        then:
        notes(table)*.text == ['hello ', 'world ', 'end '].collect {
            it.replace(' ' as char, YassRow.SPACE)
        }
        notes(table)*.beatInt == [4, 8, 16]
    }

    def 'deleting a selection including headers and end preserves those structural rows'() {
        given:
        YassTable table = song()
        table.setRowSelectionInterval(0, table.rowCount - 1)

        when:
        deleteAction(table, 0).actionPerformed(null)

        then:
        table.rowCount == 2
        table.getRowAt(0).toString() == '#TITLE:Deletion'
        table.getRowAt(1).isEnd()
        table.selectedRow == -1
    }

    def 'Delete in the lyrics area erases notes only outside text editing'() {
        given:
        YassTable table = song()
        def lyrics = new YassLyrics(Stub(YassProperties))
        def remainingBeats = []

        when:
        SwingUtilities.invokeAndWait {
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            table.setRowSelectionInterval(deleteAll ? 1 : 2, deleteAll ? 5 : 2)
            if (editing) {
                lyrics.editLyrics()
                area.caretPosition = 1
            }
            def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, 0,
                    KeyEvent.VK_DELETE, KeyEvent.CHAR_UNDEFINED)
            area.keyListeners.each { it.keyPressed(event) }
            remainingBeats.addAll(notes(table)*.beatInt)
            lyrics.finishEditing()
            lyrics.setTable(null)
        }

        then:
        remainingBeats == (editing ? [0, 4, 8, 16] : deleteAll ? [] : [0, 8, 16])

        where:
        editing | deleteAll
        false   | false
        true    | false
        false   | true
    }

    private YassTable song() {
        I18.setDefaultLanguage()
        def model = new YassTableModel()
        model.addRow(new YassRow('#TITLE:Deletion'))
        ['hel', 'lo ', 'world '].eachWithIndex { text, index ->
            model.addRow(new YassRow(':', (index * 4) as String, '4', '10',
                    text.replace(' ' as char, YassRow.SPACE)))
        }
        model.addRow(new YassRow('-', '12', '', '', ''))
        model.addRow(new YassRow(':', '16', '4', '10', 'end' + YassRow.SPACE))
        model.addRow(new YassRow('E', '', '', '', ''))
        def table = new YassTable(model, Stub(YassProperties))
        table.setModel(model)
        def listener = YassTable.getDeclaredMethod('addTableModelListener')
        listener.accessible = true
        listener.invoke(table)
        table
    }

    private static List<YassRow> notes(YassTable table) {
        (0..<table.rowCount).collect { table.getRowAt(it) }.findAll { it.isNote() }
    }

    private Action deleteAction(YassTable table, int modifiers) {
        def field = YassActions.getDeclaredField('table')
        field.accessible = true
        field.set(actions, table)
        def bindingsMethod = YassActions.getDeclaredMethod('getEditorClipboardShortcutBindings')
        bindingsMethod.accessible = true
        def bindings = bindingsMethod.invoke(actions) as List
        def binding = bindings.find {
            def method = it.class.getDeclaredMethod('keyStroke')
            method.accessible = true
            method.invoke(it) == KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, modifiers)
        }
        def actionMethod = binding.class.getDeclaredMethod('action')
        actionMethod.accessible = true
        actionMethod.invoke(binding) as Action
    }

    private static class DeletionActions extends YassActions {
        DeletionActions(YassSheet sheet, YassPlayer player, YassLyrics lyrics) {
            super(sheet, player, lyrics)
        }

        @Override
        void interruptPlay() {}
    }
}
