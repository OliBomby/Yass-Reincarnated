package yass

import spock.lang.Specification
import yass.autocorrect.YassAutoCorrect

import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.JViewport
import javax.swing.SwingUtilities
import java.awt.Dimension
import java.awt.event.KeyEvent

class YassLyricsSpec extends Specification {

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
                    .findAll { it.note }.collect { it.text.replace(YassRow.SPACE, ' ' as char) }
            consumed = event.consumed
            lyrics.finishEditing()
        }

        then:
        consumed
        actualTexts == expected

        where:
        spacingAfter | splitKey | input                         || expected
        true         | '-'      | ['One ', 'sou', '~', '~ ']    || ['One ', 'so', 'u', '~ ']
        true         | ' '      | ['One ', 'sou', '~', '~ ']    || ['One ', 'so ', 'u', '~ ']
        false        | '-'      | ['One', ' sou', '~', '~']     || ['One', ' so', 'u', '~']
        false        | ' '      | ['One', ' sou', '~', '~']     || ['One', ' so', ' u', '~']
        true         | '-'      | ['One', 'sou', '~', '~ ']     || ['One', 'so', 'u', '~ ']
        false        | '-'      | ['One', 'sou', '~', '~']      || ['One', 'so', 'u', '~']
        true         | '-'      | ['One ', '\n', 'sou', '~ ']  || ['One ', 'so', 'u ']
        false        | '-'      | ['One', '\n', 'sou', '~']    || ['One', 'so', 'u']
        true         | '-'      | ['sou', '~', '~ ']            || ['so', 'u', '~ ']
        false        | '-'      | ['sou', '~', '~']             || ['so', 'u', '~']
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
}
