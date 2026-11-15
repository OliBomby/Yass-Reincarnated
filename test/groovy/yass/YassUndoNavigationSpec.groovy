package yass

import spock.lang.Specification
import yass.autocorrect.YassAutoCorrect

import javax.swing.*
import javax.swing.text.StyleConstants
import java.awt.Dimension
import java.awt.DefaultKeyboardFocusManager
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.KeyEvent

class YassUndoNavigationSpec extends Specification {

    def 'undoing replacement of a selected syllable does not highlight the whole lyrics document'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) {
            isUncommonSpacingAfter() >> true
            getBooleanProperty('use-sample') >> true
        }
        YassLyrics lyrics
        YassTable table
        UndoActions actions
        JTextPane area
        def selectedBeats

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            ['One', 'Two', 'Three'].eachWithIndex { word, index ->
                model.addRow(new YassRow(':', "${index * 8}", '5', '10', word + YassRow.SPACE))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            table.setRowSelectionInterval(0, 0)
            table.addUndo()
            def sheet = new YassSheet()
            sheet.darkMode = darkMode
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setSheet(sheet)
            lyrics.setTable(table)
            actions = new UndoActions(sheet, Stub(YassPlayer, constructorArgs: [null, props]), lyrics)
            setField(actions, 'table', table)
            lyrics.editLyrics()
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            int start = area.text.indexOf(selectedWord)
            area.caret.setDot(start + (backward ? selectedWord.length() : 0))
            area.caret.moveDot(start + (backward ? 0 : selectedWord.length()))
        }
        SwingUtilities.invokeAndWait {}
        SwingUtilities.invokeAndWait { area.replaceSelection(replacement) }
        SwingUtilities.invokeAndWait {}
        SwingUtilities.invokeAndWait {
            assert area.selectionStart == area.selectionEnd
            selectedBeats = table.selectedRows.collect { table.getRowAt(it).beatInt }
            if (globalAction) {
                getField(actions, 'undo').actionPerformed(null)
            } else {
                press(area, KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED)
            }
        }
        SwingUtilities.invokeAndWait {}

        then:
        lyrics.editable
        area.text == 'One Two Three'
        area.selectionStart == area.selectionEnd
        area.caretPosition in area.text.indexOf(selectedWord)..(area.text.indexOf(selectedWord) + selectedWord.length())
        table.selectedRows.collect { table.getRowAt(it).beatInt } == selectedBeats
        // Inspect painted character backgrounds as well as the actual caret range.
        ['One', 'Two', 'Three'].findAll { it != selectedWord }.every { word ->
            StyleConstants.getBackground(area.styledDocument.getCharacterElement(area.text.indexOf(word)).attributes).alpha == 0
        }

        when:
        SwingUtilities.invokeAndWait {
            if (globalAction) {
                getField(actions, 'redo').actionPerformed(null)
            } else {
                press(area, KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED)
            }
        }
        SwingUtilities.invokeAndWait {}

        then:
        lyrics.editable
        area.text == 'One Two Three'.replace(selectedWord, replacement)
        area.selectionStart == area.selectionEnd
        ['One', 'Two', 'Three'].findAll { it != selectedWord }.every { word ->
            StyleConstants.getBackground(area.styledDocument.getCharacterElement(area.text.indexOf(word)).attributes).alpha == 0
        }

        cleanup:
        SwingUtilities.invokeAndWait {
            lyrics?.finishEditing()
            if (actions != null) {
                KeyboardFocusManager.currentKeyboardFocusManager.removeKeyEventDispatcher(
                        getField(actions, 'editorKeyDispatcher'))
            }
        }

        where:
        globalAction | darkMode | backward | replacement | selectedWord
        false        | false    | false    | 'x'         | 'One'
        true         | false    | true     | 'Longer'    | 'One'
        false        | true     | true     | 'Longer'    | 'Two'
        true         | true     | false    | 'x'         | 'Two'
        false        | false    | true     | 'x'         | 'Three'
        true         | true     | true     | 'Longer'    | 'Three'
    }

    def 'undo keeps endpoints within a shortened syllable and empty history leaves them unchanged'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) { isUncommonSpacingAfter() >> true }
        YassLyrics lyrics
        def positions = []

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow(':', '0', '5', '10', 'One' + YassRow.SPACE))
            model.addRow(new YassRow('-', '6', '', '', ''))
            model.addRow(new YassRow(':', '8', '5', '10', 'Two' + YassRow.SPACE))
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            table.addUndo()
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            lyrics.editLyrics()
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            // No history: retain a backward range whose endpoint is on a newline.
            area.caret.setDot(4)
            area.caret.moveDot(3)
            [KeyEvent.VK_Z, KeyEvent.VK_Y].each { key ->
                press(area, key, KeyEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED)
                positions << [area.caret.dot, area.caret.mark, lyrics.editable]
            }
            area.document.insertString(1, 'xyz', null)
            area.caret.setDot(direction < 0 ? 5 : 2)
            area.caret.moveDot(direction > 0 ? 5 : 2)
            press(area, KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED)
            positions << [area.caret.dot, area.caret.mark, lyrics.editable]
            assert area.text == 'One\nTwo'
            assert !table.canUndo()
        }
        SwingUtilities.invokeAndWait {}

        then:
        positions == [[3, 4, true], [3, 4, true],
                      [direction > 0 ? 3 : 2, direction < 0 ? 3 : 2, true]]

        cleanup:
        SwingUtilities.invokeAndWait { lyrics?.finishEditing() }

        where:
        direction << [-1, 0, 1]
    }

    def 'lyrics undo and redo preserve editing context through shortcuts and menu actions'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) {
            isUncommonSpacingAfter() >> spacingAfter
            getBooleanProperty('use-sample') >> true
        }
        def previousFocusManager = KeyboardFocusManager.currentKeyboardFocusManager
        YassLyrics lyrics
        YassTable table
        UndoActions actions
        JTextPane area
        def editableChanges = []
        def states = []
        def selectedBeats

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            6.times { page ->
                ['One', 'Two', 'Three'].eachWithIndex { word, index ->
                    def text = word + page
                    text = spacingAfter ? text + YassRow.SPACE :
                            (index == 0 ? text : String.valueOf(YassRow.SPACE) + text)
                    model.addRow(new YassRow(':', "${page * 40 + index * 8}", '5', '10', text))
                }
                model.addRow(new YassRow('-', "${page * 40 + 25}", '', '', ''))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            // Undo's stored selection is deliberately at the other end of the song.
            table.setRowSelectionInterval(spacingAfter ? 0 : 22, spacingAfter ? 0 : 22)
            table.addUndo()
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            lyrics.editLyrics()
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            KeyboardFocusManager.setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager() {
                @Override
                java.awt.Component getFocusOwner() { area }
            })
            actions = new UndoActions(new YassSheet(),
                    Stub(YassPlayer, constructorArgs: [null, props]), lyrics)
            setField(actions, 'table', table)
            setField(actions, 'currentView', YassActions.VIEW_EDIT)
            actions.registerEditorActions(new JPanel())
            area.caretPosition = area.text.indexOf('One4') + 1
            area.document.insertString(area.caretPosition, 'x', null)
            // Move away from the edit, preserving either a caret or a directed range.
            int start = area.text.indexOf('Two4') + 1
            area.caret.setDot(start + (direction < 0 ? 2 : 0))
            area.caret.moveDot(start + (direction > 0 ? 2 : 0))
            selectedBeats = table.selectedRows.collect { table.getRowAt(it).beatInt }
            area.addPropertyChangeListener('editable', { editableChanges << it.newValue } as java.beans.PropertyChangeListener)
        }
        // Let earlier queued selection updates run before invoking Undo.
        SwingUtilities.invokeAndWait {}
        [false, true, false].each { redo ->
            SwingUtilities.invokeAndWait {
                if (route == 'menu') {
                    getField(actions, redo ? 'redo' : 'undo').actionPerformed(null)
                } else {
                    def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_DOWN_MASK,
                            redo ? KeyEvent.VK_Y : KeyEvent.VK_Z, KeyEvent.CHAR_UNDEFINED)
                    if (route == 'dispatcher') {
                        assert getField(actions, 'editorKeyDispatcher').dispatchKeyEvent(event)
                    } else {
                        area.keyListeners.each { it.keyPressed(event) }
                    }
                    assert event.consumed
                }
            }
            SwingUtilities.invokeAndWait {}
            SwingUtilities.invokeAndWait {
                int start = area.text.indexOf('Two4') + 1
                states << [lyrics.editable, area.caret.dot - start, area.caret.mark - start,
                           table.selectedRows.collect { table.getRowAt(it).beatInt },
                           area.text.contains('Oxne4'), table.canRedo()]
            }
        }

        then:
        editableChanges.empty
        states == [false, true, false].collect { redo ->
            [true, direction > 0 ? 2 : 0, direction < 0 ? 2 : 0, selectedBeats, redo, !redo]
        }

        when: 'typing continues immediately and creates an independent undo step'
        SwingUtilities.invokeAndWait {
            area.document.insertString(area.caret.dot, 'z', null)
            assert !table.canRedo()
            press(area, KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, KeyEvent.CHAR_UNDEFINED)
        }
        SwingUtilities.invokeAndWait {}

        then:
        lyrics.editable
        !area.text.contains('z')
        !table.canUndo()

        cleanup:
        SwingUtilities.invokeAndWait {
            lyrics?.finishEditing()
            if (actions != null) {
                KeyboardFocusManager.currentKeyboardFocusManager.removeKeyEventDispatcher(
                        getField(actions, 'editorKeyDispatcher'))
            }
            KeyboardFocusManager.setCurrentKeyboardFocusManager(previousFocusManager)
        }

        where:
        [route, direction, spacingAfter] << [['local', 'menu', 'dispatcher'], [-1, 0, 1], [false, true]].combinations()
    }

    def 'undo and redo of a page break keep the selected syllable after navigating from an earlier edit'() {
        given:
        I18.setDefaultLanguage()
        def previousZoomMode = YassTable.zoomMode
        def props = Stub(YassProperties) {
            isUncommonSpacingAfter() >> true
            getBooleanProperty('use-sample') >> true
        }
        YassTable table
        YassLyrics lyrics
        UndoActions actions
        JTextPane area
        def selectedBeats = []
        def cursorOffsets = []
        def breakCounts = []

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow('#TITLE:Page break undo navigation'))
            6.times { page ->
                model.addRow(new YassRow(':', "${page * 40}", '5', '10', "One${page}" + YassRow.SPACE))
                if (existingBreak && page == 5) {
                    model.addRow(new YassRow('-', "${page * 40 + 6}", '', '', ''))
                }
                model.addRow(new YassRow(':', "${page * 40 + 8}", '5', '10', "Two${page}" + YassRow.SPACE))
                model.addRow(new YassRow('-', "${page * 40 + 15}", '', '', ''))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            YassTable.zoomMode = YassTable.ZOOM_ONE
            table.setRowSelectionInterval(1, 1)
            table.addUndo()
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            actions = new UndoActions(new YassSheet(),
                    Stub(YassPlayer, constructorArgs: [null, props]), lyrics)
            setField(actions, 'table', table)
            lyrics.editLyrics()
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            area.caretPosition = area.text.indexOf('Two5') + 1
            if (useButton) {
                actions.getTogglePageBreak().actionPerformed(null)
            } else {
                press(area, KeyEvent.VK_ENTER, 0, '\n' as char)
            }
        }
        SwingUtilities.invokeAndWait {}
        [false, true].each { redo ->
            SwingUtilities.invokeAndWait {
                if (globalUndo) {
                    getField(actions, redo ? 'redo' : 'undo').actionPerformed(null)
                } else {
                    press(area, redo ? KeyEvent.VK_Y : KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK,
                            KeyEvent.CHAR_UNDEFINED)
                }
            }
            SwingUtilities.invokeAndWait {}
            SwingUtilities.invokeAndWait {
                selectedBeats << table.getRowAt(table.selectedRow)?.beatInt
                cursorOffsets << area.caretPosition - area.text.indexOf('Two5')
                breakCounts << (0..<table.rowCount).count { table.getRowAt(it).isPageBreak() }
                assert table.canUndo() == redo
                assert table.canRedo() == !redo
            }
        }

        then:
        selectedBeats == [208, 208]
        // A selected syllable may put the caret just after its trailing separator.
        cursorOffsets.every { it in 0..5 }
        breakCounts == (existingBreak ? [7, 6] : [6, 7])
        !table.canRedo()

        cleanup:
        SwingUtilities.invokeAndWait {
            lyrics?.finishEditing()
            if (actions != null) {
                KeyboardFocusManager.currentKeyboardFocusManager.removeKeyEventDispatcher(
                        getField(actions, 'editorKeyDispatcher'))
            }
            YassTable.zoomMode = previousZoomMode
        }

        where:
        existingBreak | useButton | globalUndo
        false         | false     | false
        false         | true      | false
        true          | false     | false
        true          | true      | false
        false         | false     | true
        false         | true      | true
        true          | false     | true
        true          | true      | true
    }

    def 'undo and redo of a lyric split preserve the current timeline view and cursor'() {
        given:
        I18.setDefaultLanguage()
        def previousZoomMode = YassTable.zoomMode
        def props = Stub(YassProperties) {
            isUncommonSpacingAfter() >> true
            getBooleanProperty('use-sample') >> true
        }
        def player = Stub(YassPlayer, constructorArgs: [null, props])
        YassLyrics lyrics
        YassSheet sheet
        YassTable table
        UndoActions actions
        JTextPane area
        Point currentView
        double currentZoom
        int currentCursor
        def states = []

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow('#TITLE:Undo navigation'))
            int beat = 0
            6.times { page ->
                ['Pa', 'ru', '~ '].each { text ->
                    model.addRow(new YassRow(':', beat as String, (5 + page) as String, '10',
                            text.replace(' ' as char, YassRow.SPACE)))
                    beat += 6 + page
                }
                model.addRow(new YassRow('-', beat as String, '', '', ''))
                beat += 8
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            sheet = new YassSheet()
            def viewport = new JViewport()
            viewport.setSize(800, 420)
            viewport.setExtentSize(new Dimension(800, 420))
            viewport.setView(sheet)
            sheet.setDuration(120000)
            sheet.addTable(table)
            table.setSheet(sheet)
            sheet.setActiveTable(table)
            sheet.enablePan(true)
            sheet.setAbsolutePitchViewEnabled(absolutePitch)
            sheet.update()
            YassTable.zoomMode = YassTable.ZOOM_ONE
            table.setRowSelectionInterval(1, 1)
            table.zoomPage()
            table.addUndo()

            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setSheet(sheet)
            lyrics.setTable(table)
            lyrics.editLyrics()
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            area.caretPosition = area.text.lastIndexOf('ru') + 1
            press(area, KeyEvent.VK_MINUS, 0, '-' as char)
            sheet.setPlayerPosition(sheet.beatToTimeline(table.getRowAt(22).beatInt + 1))
            currentView = new Point(sheet.viewPosition)
            currentZoom = sheet.beatSize
            currentCursor = sheet.playerPosition
            assert currentView.x > 0
            if (globalAction) {
                actions = new UndoActions(sheet, player, lyrics)
                setField(actions, 'table', table)
            }
        }
        SwingUtilities.invokeAndWait {}

        [false, true].each { redo ->
            SwingUtilities.invokeAndWait {
                if (globalAction) {
                    getField(actions, redo ? 'redo' : 'undo').actionPerformed(null)
                } else {
                    press(area, redo ? KeyEvent.VK_Y : KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK,
                            KeyEvent.CHAR_UNDEFINED)
                }
            }
            SwingUtilities.invokeAndWait {}
            SwingUtilities.invokeAndWait {
                states << [new Point(sheet.viewPosition), sheet.beatSize, sheet.playerPosition]
            }
        }

        then:
        states == [[currentView, currentZoom, currentCursor], [currentView, currentZoom, currentCursor]]

        cleanup:
        SwingUtilities.invokeAndWait {
            lyrics?.finishEditing()
            if (actions != null) {
                KeyboardFocusManager.currentKeyboardFocusManager.removeKeyEventDispatcher(
                        getField(actions, 'editorKeyDispatcher'))
            }
            YassTable.zoomMode = previousZoomMode
        }

        where:
        globalAction | absolutePitch
        false        | false
        false        | true
        true         | false
        true         | true
    }

    private static void press(JTextPane area, int code, int modifiers, char character) {
        def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, modifiers, code, character)
        area.keyListeners.each { it.keyPressed(event) }
    }

    private static Object getField(Object target, String name) {
        def field = YassActions.getDeclaredField(name)
        field.accessible = true
        field.get(target)
    }

    private static void setField(Object target, String name, Object value) {
        def field = YassActions.getDeclaredField(name)
        field.accessible = true
        field.set(target, value)
    }

    private static class UndoActions extends YassActions {
        UndoActions(YassSheet sheet, YassPlayer player, YassLyrics lyrics) {
            super(sheet, player, lyrics)
        }

        @Override
        void interruptPlay() {}

        @Override
        void checkData(YassTable table, boolean all, boolean extensive) {}
    }
}
