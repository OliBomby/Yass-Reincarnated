package yass

import spock.lang.Specification
import yass.autocorrect.YassAutoCorrect

import javax.swing.*
import java.awt.Dimension
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.KeyEvent

class YassUndoNavigationSpec extends Specification {

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
