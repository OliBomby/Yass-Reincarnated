package yass

import spock.lang.Specification
import yass.autocorrect.YassAutoCorrect

import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.JViewport
import javax.swing.SwingUtilities
import javax.swing.text.DefaultEditorKit
import java.awt.Dimension
import java.awt.DefaultKeyboardFocusManager
import java.awt.KeyboardFocusManager
import java.awt.Toolkit
import java.awt.event.FocusEvent
import java.awt.event.KeyEvent
import java.awt.event.ActionEvent
import java.awt.image.BufferedImage
import yass.input.*

class YassLyricsSpec extends Specification {

    def 'an arrow arriving during the F4 switch cannot move the lyrics cursor to the start'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties)
        YassLyrics lyrics
        YassTable table
        JTextPane area
        int entryPosition
        int positionBeforeRepaint
        int positionAfterRepaint
        def selectedBeforeRepaint
        def arrowAction = forward ? DefaultEditorKit.forwardAction : DefaultEditorKit.backwardAction
        def paintText = {
            area.setSize(400, 300)
            def graphics = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB).createGraphics()
            try {
                area.paint(graphics)
            } finally {
                graphics.dispose()
            }
        }
        def pressArrow = {
            area.actionMap.get(arrowAction).actionPerformed(
                    new ActionEvent(area, ActionEvent.ACTION_PERFORMED, arrowAction))
        }

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            ['One ', 'Two ', 'Three ', 'Four ', 'Five ', 'Last'].eachWithIndex { text, index ->
                model.addRow(new YassRow(':', (index * 8) as String, '6', '10',
                        text.replace(' ' as char, YassRow.SPACE)))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            table.setRowSelectionInterval(2, 2)
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
        }
        SwingUtilities.invokeAndWait {}
        SwingUtilities.invokeAndWait {
            paintText()
            if (arrowBeforeSwitch) {
                if (forward) {
                    table.nextBeat()
                } else {
                    table.prevBeat()
                }
            }
            lyrics.editLyrics()
            entryPosition = area.caretPosition
            // Process the next input before the rebuilt editable text view has painted.
            pressArrow()
            positionBeforeRepaint = area.caretPosition
            selectedBeforeRepaint = table.selectedRows.toList()
            paintText()
            pressArrow()
            positionAfterRepaint = area.caretPosition
        }

        then:
        entryPosition > 0
        positionBeforeRepaint == entryPosition
        selectedBeforeRepaint == [arrowBeforeSwitch ? (forward ? 3 : 1) : 2]
        positionAfterRepaint == entryPosition + (forward ? 1 : -1)

        cleanup:
        SwingUtilities.invokeAndWait { lyrics?.finishEditing() }

        where:
        forward | arrowBeforeSwitch
        false   | false
        true    | false
        false   | true
        true    | true
    }

    def 'switching to lyrics before queued arrow navigation finishes keeps the current syllable'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties)
        YassLyrics lyrics
        YassTable table
        JTextPane area
        int entryPosition
        def selectedAfterCursorMove

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            ['One ', 'Two ', 'Three ', '\n', 'Last'].eachWithIndex { text, index ->
                model.addRow(text == '\n'
                        ? new YassRow('-', (index * 8) as String, '', '', '')
                        : new YassRow(':', (index * 8) as String, '6', '10',
                                text.replace(' ' as char, YassRow.SPACE)))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            table.setRowSelectionInterval(refreshLyrics ? 1 : 4, refreshLyrics ? 1 : 4)
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
        }
        SwingUtilities.invokeAndWait {}
        SwingUtilities.invokeAndWait {
            if (refreshLyrics) {
                assert area.caretPosition < area.document.length
                // A note update rebuilds the lyrics document even without visiting the last note.
                (table.model as YassTableModel).fireTableRowsUpdated(1, 1)
            } else {
                table.setRowSelectionInterval(1, 1)
            }
            assert area.caretPosition == area.document.length
            if (forward) {
                table.nextBeat()
            } else {
                table.prevBeat()
            }
            // These input events can already be queued ahead of the lyrics selection update.
            lyrics.editLyrics()
            entryPosition = area.caretPosition
        }
        SwingUtilities.invokeAndWait {}
        SwingUtilities.invokeAndWait {
            area.caretPosition = Math.max(0, area.caretPosition - 1)
            selectedAfterCursorMove = table.selectedRows.toList()
        }

        then:
        entryPosition >= area.text.indexOf(syllable)
        entryPosition <= area.text.indexOf(syllable) + syllable.length()
        selectedAfterCursorMove == [expectedRow]

        cleanup:
        SwingUtilities.invokeAndWait { lyrics?.finishEditing() }

        where:
        forward | refreshLyrics | syllable | expectedRow
        false   | false         | 'One'    | 0
        true    | false         | 'Three'  | 2
        false   | true          | 'One'    | 0
        true    | true          | 'Three'  | 2
    }

    def 'switching to lyrics preserves a selection made directly in the lyrics'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties)
        YassLyrics lyrics
        JTextPane area
        def cursorBeforeEditing

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow(':', '0', '6', '10', 'Hello'))
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            table.setRowSelectionInterval(0, 0)
            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            area.caretPosition = 2
            cursorBeforeEditing = [area.caret.dot, area.caret.mark]
            lyrics.editLyrics()
            assert [area.caret.dot, area.caret.mark] == cursorBeforeEditing
        }
        SwingUtilities.invokeAndWait {}

        then:
        [area.caret.dot, area.caret.mark] == cursorBeforeEditing

        cleanup:
        SwingUtilities.invokeAndWait { lyrics?.finishEditing() }
    }

    def 'lyrics cursor follows whole pages without moving visible syllables to the left edge'() {
        given:
        I18.setDefaultLanguage()
        def previousZoomMode = YassTable.getZoomMode()
        YassProperties props = Stub(YassProperties)
        YassAutoCorrect auto = Stub(YassAutoCorrect)
        YassLyrics lyrics
        YassSheet sheet
        YassTable table
        JTextPane area
        int firstPageX
        double pageBeatSize

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow('#TITLE:Cursor scrolling'))
            ['One ', 'Two ', 'Three ', '\n', 'Four ', 'Five ', 'Six ', '\n', 'Seven ']
                    .eachWithIndex { text, index ->
                model.addRow(text == '\n'
                        ? new YassRow('-', (index * 8) as String, '', '', '')
                        : new YassRow(':', (index * 8) as String, '6', '10',
                                text.replace(' ' as char, YassRow.SPACE)))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            table = new YassTable(model, props)
            table.setModel(model)
            sheet = new YassSheet()
            def viewport = new JViewport()
            viewport.setSize(800, 420)
            viewport.setExtentSize(new Dimension(800, 420))
            viewport.setView(sheet)
            sheet.setDuration(20000)
            sheet.addTable(table)
            table.setSheet(sheet)
            sheet.setActiveTable(table)
            sheet.enablePan(true)
            sheet.setAbsolutePitchViewEnabled(absolutePitch)
            sheet.update()

            YassTable.setZoomMode(YassTable.ZOOM_ONE)
            table.setRowSelectionInterval(1, 1)
            table.zoomPage()
            firstPageX = sheet.getViewPosition().x
            pageBeatSize = sheet.getBeatSize()

            lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(auto)
            lyrics.setSheet(sheet)
            lyrics.setTable(table)
            if (editing) {
                lyrics.editLyrics()
            }
            area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
        }
        SwingUtilities.invokeAndWait {}

        then:
        !sheet.isVisible(5)

        when:
        def positions = []
        def selections = []
        ['Two', 'Five', 'Four', 'Two', 'Three'].eachWithIndex { syllable, index ->
            SwingUtilities.invokeAndWait {
                area.caretPosition = area.text.indexOf(syllable) + 1
            }
            SwingUtilities.invokeAndWait {
                positions << sheet.getViewPosition().x
                selections << table.getSelectedRows().toList()
                assert sheet.getBeatSize() == pageBeatSize
                int pageStart = [1, 5, 5, 1, 1][index]
                assert sheet.isVisible(pageStart)
                assert sheet.isVisible(pageStart + 2)
            }
        }

        then:
        positions[0] == firstPageX
        positions[1] > firstPageX
        positions[2] == positions[1]
        positions[3] < positions[2]
        positions[4] == positions[3]
        selections == [[2], [6], [5], [2], [3]]

        cleanup:
        SwingUtilities.invokeAndWait {
            lyrics?.finishEditing()
            YassTable.setZoomMode(previousZoomMode)
        }

        where:
        editing | absolutePitch
        true    | false
        true    | true
        false   | false
        false   | true
    }

    def 'splitting lyrics at the caret preserves the spacing convention'() {
        given:
        I18.setDefaultLanguage()
        YassProperties props = Stub(YassProperties) {
            isUncommonSpacingAfter() >> spacingAfter
        }
        YassAutoCorrect auto = Stub(YassAutoCorrect)
        List<String> actualTexts
        String displayedText
        boolean consumed

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            input.eachWithIndex { text, index ->
                if (text == '\n') {
                    model.addRow(new YassRow('-', (index * 6) as String, '', '', ''))
                } else {
                    model.addRow(new YassRow(':', (index * 6) as String, '5', '10',
                            text.replace(' ' as char, YassRow.SPACE)))
                }
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            def lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(auto)
            lyrics.setTable(table)
            lyrics.editLyrics()
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            area.caretPosition = area.text.indexOf('sou') + 2
            def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, 0,
                    splitKey == '-' ? KeyEvent.VK_MINUS : KeyEvent.VK_SPACE, splitKey as char)
            area.keyListeners.each { it.keyPressed(event) }
            actualTexts = (0..<table.rowCount).collect { table.getRowAt(it) }
                    .findAll { it.note }.collect { it.text }
            displayedText = area.text
            consumed = event.consumed
            lyrics.finishEditing()
        }

        then:
        consumed
        actualTexts == expected.collect { it.replace(' ' as char, YassRow.SPACE) }
        displayedText.contains('so' + splitKey + 'u')

        where:
        spacingAfter | splitKey | input                         || expected
        true         | '-'      | ['One ', 'sou', '~', '~ ']    || ['One ', 'so', 'u', '~ ']
        true         | ' '      | ['One ', 'sou', '~', '~ ']    || ['One ', 'so ', 'u', '~ ']
        false        | '-'      | ['One', ' sou', '~', '~']     || ['One', ' so', 'u', '~']
        false        | ' '      | ['One', ' sou', '~', '~']     || ['One', ' so', ' u', '~']
        true         | '-'      | ['One', 'sou', '~', '~ ']     || ['One', 'so', 'u', '~ ']
        true         | ' '      | ['One', 'sou', '~', '~ ']     || ['One', 'so ', 'u', '~ ']
        false        | '-'      | ['One', 'sou', '~', '~']      || ['One', 'so', 'u', '~']
        false        | ' '      | ['One', 'sou', '~', '~']      || ['One', 'so', ' u', '~']
        true         | '-'      | ['One ', '\n', 'sou', '~ ']  || ['One ', 'so', 'u ']
        true         | ' '      | ['One ', '\n', 'sou', '~ ']  || ['One ', 'so ', 'u ']
        false        | '-'      | ['One', '\n', 'sou', '~']    || ['One', 'so', 'u']
        false        | ' '      | ['One', '\n', 'sou', '~']    || ['One', 'so', ' u']
        true         | '-'      | ['sou', '~', '~ ']            || ['so', 'u', '~ ']
        true         | ' '      | ['sou', '~', '~ ']            || ['so ', 'u', '~ ']
        false        | '-'      | ['sou', '~', '~']             || ['so', 'u', '~']
        false        | ' '      | ['sou', '~', '~']             || ['so', ' u', '~']
    }

    def 'recognizes minus key events from keyboard layouts and numeric keypads'() {
        given:
        def component = new JPanel()

        expect:
        YassLyrics.isMinusKey(new KeyEvent(component, KeyEvent.KEY_PRESSED, 0, 0,
                keyCode, keyChar as char)) == expected

        where:
        keyCode              | keyChar                    || expected
        KeyEvent.VK_MINUS    | KeyEvent.CHAR_UNDEFINED    || true
        KeyEvent.VK_SUBTRACT | KeyEvent.CHAR_UNDEFINED    || true
        KeyEvent.VK_UNDEFINED| '-'                        || true
        KeyEvent.VK_A        | 'a'                        || false
    }

    def 'a lyric split stays in editing mode during a spacing prompt and undoes in one step'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) {
            isUncommonSpacingAfter() >> true
        }
        def previousFocusManager = KeyboardFocusManager.currentKeyboardFocusManager
        def timelineSplits = []
        def duringPrompt = []
        def original = ['One ', 'ru', '~', '~ ']
        def snapshots = []
        def geometry = []
        def consumed = []

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            original.eachWithIndex { text, index ->
                model.addRow(new YassRow(':', (index * 6) as String, '5', '10',
                        text.replace(' ' as char, YassRow.SPACE)))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new PromptingRollTable(model, props)
            table.setModel(model)
            // The lightweight test constructor omits the production undo listener.
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            table.addUndo()
            def lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            lyrics.editLyrics()
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            KeyboardFocusManager.setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager() {
                @Override
                java.awt.Component getFocusOwner() { area }
            })
            def registry = new EditorKeyBindingRegistry()
            registry.bind('-' as char, new SimpleEditorCommand('splitRows', { true }, {
                timelineSplits << 'split'
            }))
            def dispatcher = new EditorKeyDispatcher({ true }, {
                new EditorInputContext(lyrics.editable ? FocusArea.LYRICS_EDIT : FocusArea.LYRICS_VIEW,
                        lyrics.editable, false, false, false, false, true, false)
            }, registry, new KeySequenceTracker(500), { true })
            table.prompt = {
                // A modal prompt pumps queued events before rollRight returns.
                def loop = Toolkit.defaultToolkit.systemEventQueue.createSecondaryLoop()
                SwingUtilities.invokeLater { loop.exit() }
                loop.enter()
                area.focusListeners.each { it.focusLost(new FocusEvent(area, FocusEvent.FOCUS_LOST, true)) }
                duringPrompt << lyrics.editable
                def typed = new KeyEvent(area, KeyEvent.KEY_TYPED, 0, 0,
                        KeyEvent.VK_UNDEFINED, splitKey as char)
                if (!dispatcher.dispatchKeyEvent(typed)) {
                    area.keyListeners.each { it.keyTyped(typed) }
                }
                consumed << typed.consumed
            }
            def takeSnapshot = {
                snapshots << table.noteRows.collect { it.text.replace(YassRow.SPACE, ' ' as char) }
                geometry << table.noteRows.collect { [it.beatInt, it.lengthInt, it.heightInt] }
            }
            takeSnapshot()
            area.caretPosition = area.text.indexOf('ru') + 1
            if (overwrite) {
                def insert = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, 0,
                        KeyEvent.VK_INSERT, KeyEvent.CHAR_UNDEFINED)
                area.keyListeners.each { it.keyPressed(insert) }
            }
            def press = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, 0,
                    splitKey == '-' ? KeyEvent.VK_MINUS : KeyEvent.VK_SPACE, splitKey as char)
            area.keyListeners.each { it.keyPressed(press) }
            consumed << press.consumed
            takeSnapshot()
            [KeyEvent.VK_Z, KeyEvent.VK_Y, KeyEvent.VK_Z].each { key ->
                def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_DOWN_MASK,
                        key, KeyEvent.CHAR_UNDEFINED)
                area.keyListeners.each { it.keyPressed(event) }
                takeSnapshot()
            }
            assert !table.canUndo()
            lyrics.finishEditing()
        }

        then:
        duringPrompt == [true]
        timelineSplits.empty
        consumed == [true, true]
        snapshots == [original, expected, original, expected, original]
        geometry.every { it == geometry.first() }

        cleanup:
        KeyboardFocusManager.setCurrentKeyboardFocusManager(previousFocusManager)

        where:
        splitKey | overwrite || expected
        '-'      | false     || ['One ', 'r', 'u', '~ ']
        '-'      | true      || ['One ', 'r', 'u', '~ ']
        ' '      | false     || ['One ', 'r ', 'u', '~ ']
        ' '      | true      || ['One ', 'r ', 'u', '~ ']
    }

    def 'typed lyric changes have no duplicate undo step'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) { isUncommonSpacingAfter() >> true }
        def snapshots = []

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow(':', '0', '5', '10', 'ru' + YassRow.SPACE))
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            table.addUndo()
            def lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            lyrics.editLyrics()
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            area.document.insertString(1, 'a', null)
            snapshots << table.getRowAt(0).text
            lyrics.finishEditing()
            table.undoRows()
            snapshots << table.getRowAt(0).text
            assert !table.canUndo()
            table.redoRows()
            snapshots << table.getRowAt(0).text
        }

        then:
        snapshots == ['rau' + YassRow.SPACE, 'ru' + YassRow.SPACE, 'rau' + YassRow.SPACE]
    }

    def 'deleting a syllable separator rolls lyrics left and undoes in one step'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) { isUncommonSpacingAfter() >> spacingAfter }
        def original = spacingAfter ? ['One ', 'ru', 'les', '~ '] : ['One', ' ru', 'les', '~']
        def expected = spacingAfter ? ['One ', 'rules', '~', '~ '] : ['One', ' rules', '~', '~']
        def snapshots = []
        def geometry = []
        def displayedTexts = []
        boolean consumed

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            original.eachWithIndex { text, index ->
                model.addRow(new YassRow(':', (index * 6) as String, '5', '10',
                        text.replace(' ' as char, YassRow.SPACE)))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            table.addUndo()
            def lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            lyrics.editLyrics()
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            def takeSnapshot = {
                snapshots << table.noteRows.collect { it.text.replace(YassRow.SPACE, ' ' as char) }
                geometry << table.noteRows.collect { [it.beatInt, it.lengthInt, it.heightInt] }
                displayedTexts << area.text
            }
            takeSnapshot()
            int separatorPosition = area.text.indexOf('ru') + 2
            assert area.text.charAt(separatorPosition) == '-' as char
            area.caretPosition = separatorPosition + (key == KeyEvent.VK_BACK_SPACE ? 1 : 0)
            def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, 0,
                    key, KeyEvent.CHAR_UNDEFINED)
            area.keyListeners.each { it.keyPressed(event) }
            consumed = event.consumed
            assert area.caretPosition == separatorPosition
            takeSnapshot()
            [KeyEvent.VK_Z, KeyEvent.VK_Y].each { undoKey ->
                def undoEvent = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_DOWN_MASK,
                        undoKey, KeyEvent.CHAR_UNDEFINED)
                area.keyListeners.each { it.keyPressed(undoEvent) }
                takeSnapshot()
                if (undoKey == KeyEvent.VK_Z) {
                    assert !table.canUndo()
                }
            }
            lyrics.finishEditing()
        }

        then:
        consumed
        snapshots == [original, expected, original, expected]
        geometry.every { it == geometry.first() }
        displayedTexts[1].contains('rules')
        displayedTexts[2] == displayedTexts[0]
        displayedTexts[3] == displayedTexts[1]

        where:
        spacingAfter | key
        false        | KeyEvent.VK_DELETE
        false        | KeyEvent.VK_BACK_SPACE
        true         | KeyEvent.VK_DELETE
        true         | KeyEvent.VK_BACK_SPACE
    }

    def 'deleting a word space joins the text without inserting a hyphen'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties) { isUncommonSpacingAfter() >> spacingAfter }
        def original = spacingAfter ? ['One ', 'ru ', 'les', '~ '] : ['One', ' ru', ' les', '~']
        def expected = spacingAfter ? ['One ', 'rules', '~ ', ''] : ['One', ' rules', '~', '']
        def snapshots = []
        def geometry = []
        def displayedTexts = []
        boolean consumed

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            original.eachWithIndex { text, index ->
                model.addRow(new YassRow(':', (index * 6) as String, '5', '10',
                        text.replace(' ' as char, YassRow.SPACE)))
            }
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            def attachListener = YassTable.getDeclaredMethod('addTableModelListener')
            attachListener.accessible = true
            attachListener.invoke(table)
            table.addUndo()
            def lyrics = new YassLyrics(props)
            lyrics.setAutoCorrect(Stub(YassAutoCorrect))
            lyrics.setTable(table)
            lyrics.editLyrics()
            def area = (lyrics.components.find { it instanceof JScrollPane } as JScrollPane)
                    .viewport.view as JTextPane
            def takeSnapshot = {
                snapshots << table.noteRows.collect { it.text.replace(YassRow.SPACE, ' ' as char) }
                geometry << table.noteRows.collect { [it.beatInt, it.lengthInt, it.heightInt] }
                displayedTexts << area.text
            }
            takeSnapshot()
            int spacePosition = area.text.indexOf('ru') + 2
            assert area.text.charAt(spacePosition) == ' ' as char
            area.caretPosition = spacePosition + (key == KeyEvent.VK_BACK_SPACE ? 1 : 0)
            def event = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, 0,
                    key, KeyEvent.CHAR_UNDEFINED)
            area.keyListeners.each { it.keyPressed(event) }
            consumed = event.consumed
            assert area.caretPosition == spacePosition
            takeSnapshot()
            [KeyEvent.VK_Z, KeyEvent.VK_Y].each { undoKey ->
                def undoEvent = new KeyEvent(area, KeyEvent.KEY_PRESSED, 0, KeyEvent.CTRL_DOWN_MASK,
                        undoKey, KeyEvent.CHAR_UNDEFINED)
                area.keyListeners.each { it.keyPressed(undoEvent) }
                takeSnapshot()
                if (undoKey == KeyEvent.VK_Z) {
                    assert !table.canUndo()
                }
            }
            lyrics.finishEditing()
        }

        then:
        consumed
        snapshots == [original, expected, original, expected]
        geometry.every { it == geometry.first() }
        displayedTexts[1] == displayedTexts[0].replace('ru les', 'rules')
        displayedTexts[2] == displayedTexts[0]
        displayedTexts[3].contains('rules')
        !displayedTexts[3].contains('ru-les')

        where:
        spacingAfter | key
        false        | KeyEvent.VK_DELETE
        false        | KeyEvent.VK_BACK_SPACE
        true         | KeyEvent.VK_DELETE
        true         | KeyEvent.VK_BACK_SPACE
    }

    private static class PromptingRollTable extends YassTable {
        Closure prompt

        PromptingRollTable(YassTableModel model, YassProperties props) {
            super(model, props)
        }

        @Override
        void rollRight(char splitCode, int pos) {
            prompt.call()
            super.rollRight(splitCode, pos)
        }
    }
}
