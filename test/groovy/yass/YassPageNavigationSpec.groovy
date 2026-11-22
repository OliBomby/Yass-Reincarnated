package yass

import spock.lang.Specification

import javax.swing.SwingUtilities
import java.awt.KeyboardFocusManager

class YassPageNavigationSpec extends Specification {
    def 'page arrows navigate from the playhead with no notes selected'() {
        given:
        I18.setDefaultLanguage()
        def props = Stub(YassProperties)
        def player = Stub(YassPlayer, constructorArgs: [null, props])
        YassActions actions
        int destination

        when:
        SwingUtilities.invokeAndWait {
            def model = new YassTableModel()
            model.addRow(new YassRow('#TITLE:Page navigation'))
            model.addRow(new YassRow(':', '0', '4', '10', 'One'))
            model.addRow(new YassRow('-', '8', '', '', ''))
            model.addRow(new YassRow(':', '12', '4', '10', 'Two'))
            model.addRow(new YassRow('-', '20', '', '', ''))
            model.addRow(new YassRow(':', '24', '4', '10', 'Three'))
            model.addRow(new YassRow('E', '', '', '', ''))
            def table = new YassTable(model, props)
            table.setModel(model)
            table.setPreventZoom(true)
            def sheet = new YassSheet()
            table.setSheet(sheet)
            sheet.addTable(table)
            sheet.setActiveTable(table)
            sheet.update()
            sheet.setPlayerPosition(sheet.beatToTimeline(playheadBeat))
            assert table.selectedRow == -1
            assert table.getPageNumber(sheet.nextElement()) == startingPage

            actions = new YassActions(sheet, player, new YassLyrics(props))
            def tableField = YassActions.getDeclaredField('table')
            tableField.accessible = true
            tableField.set(actions, table)
            sheet.actionMap.get("${action}Pressed".toString()).actionPerformed(null)
            sheet.actionMap.get("${action}Released".toString()).actionPerformed(null)
            destination = table.pageNumber
        }

        then:
        destination == expectedPage

        cleanup:
        if (actions != null) {
            def dispatcherField = YassActions.getDeclaredField('editorKeyDispatcher')
            dispatcherField.accessible = true
            KeyboardFocusManager.currentKeyboardFocusManager.removeKeyEventDispatcher(dispatcherField.get(actions))
        }

        where:
        action     | playheadBeat | startingPage | expectedPage
        'prevPage' | 14           | 2            | 1
        'nextPage' | 14           | 2            | 3
        'prevPage' | 2            | 1            | 3
        'nextPage' | 26           | 3            | 1
    }
}
