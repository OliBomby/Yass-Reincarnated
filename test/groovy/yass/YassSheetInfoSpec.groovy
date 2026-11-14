package yass

import spock.lang.Specification

import javax.swing.SwingUtilities
import java.awt.Point
import java.awt.event.MouseEvent

class YassSheetInfoSpec extends Specification {
    def setup() {
        I18.setDefaultLanguage()
    }

    def 'empty status strip space does not navigate on click or drag'() {
        given:
        def table = Mock(YassTable) {
            getIndexOfNoteBeforeBeat(_) >> 2
            getRowCount() >> 3
        }
        def sheet = Mock(YassSheet) {
            getTable(_) >> table
            isVisibleMs(_) >> true
            getViewPosition() >> new Point()
        }
        def info = new YassSheetInfo(sheet, 0)
        info.setSize(900, 96)

        when:
        SwingUtilities.invokeAndWait {
            mouse(info, MouseEvent.MOUSE_PRESSED, 700, pressY, button)
            if (dragY != null) {
                mouse(info, MouseEvent.MOUSE_DRAGGED, 800, dragY, button)
            }
            mouse(info, MouseEvent.MOUSE_RELEASED, 800, dragY ?: pressY, button)
        }
        // Flush navigation callbacks queued by a mouse press.
        SwingUtilities.invokeAndWait {}

        then:
        0 * table.setRowSelectionInterval(_, _)
        0 * table.updatePlayerPosition()
        0 * table.gotoPageNumber(_)
        0 * table.zoomPage()
        0 * sheet.setPlayerPosition(_)
        0 * sheet.setVisibleWindowMs(_, _)
        0 * sheet.slideRight(_)

        cleanup:
        info?.removeListener()

        where:
        [pressY, dragY, button] << [[0, 20, 31], [null, 20, 50],
                [MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3]].combinations()
    }

    def 'timeline navigation below the status strip remains available'() {
        given:
        def table = Mock(YassTable) {
            getIndexOfNoteBeforeBeat(_) >> 2
            getRowCount() >> 3
        }
        def sheet = Stub(YassSheet) {
            getActiveTable() >> null
            getTable(_) >> table
            isVisibleMs(_) >> true
        }
        def info = new YassSheetInfo(sheet, 0)
        info.setSize(900, 96)

        when:
        SwingUtilities.invokeAndWait {
            mouse(info, MouseEvent.MOUSE_PRESSED, 700, 90, MouseEvent.BUTTON1)
            mouse(info, MouseEvent.MOUSE_RELEASED, 700, 90, MouseEvent.BUTTON1)
        }
        SwingUtilities.invokeAndWait {}

        then:
        1 * table.setRowSelectionInterval(2, 2)
        1 * table.updatePlayerPosition()

        cleanup:
        info?.removeListener()
    }

    def 'clicking the overview directly below the status strip still navigates'() {
        given:
        def table = Mock(YassTable) {
            getIndexOfNoteBeforeBeat(_) >> 2
            getPage(1) >> 0
            getPageNumber(2) >> 1
            getRowCount() >> 3
            getRowAt(_) >> new YassRow(':', '0', '4', '10', 'La ')
        }
        def sheet = Stub(YassSheet) {
            getActiveTable() >> null
            getTable(_) >> table
        }
        def info = new YassSheetInfo(sheet, 0)
        info.setSize(900, 96)

        when:
        SwingUtilities.invokeAndWait {
            mouse(info, MouseEvent.MOUSE_PRESSED, 700, y, MouseEvent.BUTTON1)
            mouse(info, MouseEvent.MOUSE_RELEASED, 700, y, MouseEvent.BUTTON1)
        }
        SwingUtilities.invokeAndWait {}

        then:
        1 * table.gotoPageNumber(1)
        0 * table.setRowSelectionInterval(_, _)

        cleanup:
        info?.removeListener()

        where:
        y << [32, 60, 81]
    }

    private static void mouse(YassSheetInfo info, int id, int x, int y, int button) {
        info.dispatchEvent(new MouseEvent(info, id, 0, 0, x, y, 1, false, button))
    }
}
