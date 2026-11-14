package yass

import spock.lang.Specification
import yass.autocorrect.YassAutoCorrect

import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import java.awt.event.KeyEvent

class YassLyricsSpec extends Specification {

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
