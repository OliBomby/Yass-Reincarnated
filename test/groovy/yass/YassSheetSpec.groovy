package yass

import spock.lang.Specification

import javax.swing.JViewport
import javax.swing.SwingUtilities
import java.awt.image.BufferedImage
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseEvent

class YassSheetSpec extends Specification {

    def 'clicking or holding a scroll arrow preserves the view after release'() {
        given:
        I18.setDefaultLanguage()
        def previousZoomMode = YassTable.zoomMode
        YassSheet sheet
        Point scrolledView
        double zoom

        when:
        SwingUtilities.invokeAndWait {
            sheet = scrollingSheet(absolutePitch)
            zoom = sheet.beatSize
            def originalView = new Point(sheet.viewPosition)
            mouseAtScrollArrow(sheet, MouseEvent.MOUSE_PRESSED, right)
            def timer = sheet.slideTimer
            // Advance repeats on the EDT without depending on wall-clock timing.
            repeats.times {
                timer.actionListeners.each { it.actionPerformed(null) }
            }
            scrolledView = new Point(sheet.viewPosition)
            assert scrolledView.x == originalView.x + (right ? 10 : -10) * (repeats + 1)
            assert scrolledView.y == originalView.y
            mouseAtScrollArrow(sheet, MouseEvent.MOUSE_RELEASED, right)
            assert !timer.running
            // A callback already queued before release must not move the view.
            timer.actionListeners.each { it.actionPerformed(null) }
        }
        SwingUtilities.invokeAndWait {}

        then:
        sheet.viewPosition == scrolledView
        sheet.beatSize == zoom

        cleanup:
        SwingUtilities.invokeAndWait {
            if (sheet != null) {
                mouseAtScrollArrow(sheet, MouseEvent.MOUSE_RELEASED, right)
            }
            YassTable.zoomMode = previousZoomMode
        }

        where:
        right | absolutePitch | repeats
        false | false         | 0
        true  | false         | 0
        false | true          | 0
        true  | true          | 0
        false | false         | 3
        true  | false         | 3
        false | true          | 3
        true  | true          | 3
    }

    def 'scroll repeats pause outside the arrow and stop when released outside'() {
        given:
        I18.setDefaultLanguage()
        def previousZoomMode = YassTable.zoomMode
        YassSheet sheet

        when:
        SwingUtilities.invokeAndWait {
            sheet = scrollingSheet(false)
            mouseAtScrollArrow(sheet, MouseEvent.MOUSE_PRESSED, right)
            def timer = sheet.slideTimer
            def beforeLeaving = new Point(sheet.viewPosition)
            int outsideX = sheet.viewPosition.x + 400
            sheet.dispatchEvent(new MouseEvent(sheet, MouseEvent.MOUSE_DRAGGED, 0,
                    MouseEvent.BUTTON1_DOWN_MASK, outsideX, 200,
                    0, false, MouseEvent.NOBUTTON))
            timer.actionListeners.each { it.actionPerformed(null) }
            assert sheet.viewPosition == beforeLeaving

            mouseAtScrollArrow(sheet, MouseEvent.MOUSE_DRAGGED, right)
            timer.actionListeners.each { it.actionPerformed(null) }
            assert sheet.viewPosition.x == beforeLeaving.x + (right ? 10 : -10)

            sheet.dispatchEvent(new MouseEvent(sheet, MouseEvent.MOUSE_DRAGGED, 0,
                    MouseEvent.BUTTON1_DOWN_MASK, outsideX, 200,
                    0, false, MouseEvent.NOBUTTON))
            def beforeRelease = new Point(sheet.viewPosition)
            sheet.dispatchEvent(new MouseEvent(sheet, MouseEvent.MOUSE_RELEASED, 0,
                    0, outsideX, 200, 1, false, MouseEvent.BUTTON1))
            assert !timer.running
            timer.actionListeners.each { it.actionPerformed(null) }
            assert sheet.viewPosition == beforeRelease
        }

        then:
        noExceptionThrown()

        cleanup:
        SwingUtilities.invokeAndWait {
            if (sheet != null) {
                mouseAtScrollArrow(sheet, MouseEvent.MOUSE_RELEASED, right)
            }
            YassTable.zoomMode = previousZoomMode
        }

        where:
        right << [false, true]
    }

    private static YassSheet scrollingSheet(boolean absolutePitch) {
        def table = new YassTable()
        table.addRow(new YassRow('#TITLE:Manual scrolling'))
        4.times { page ->
            3.times { note ->
                table.addRow(new YassRow(':', (page * 32 + note * 8) as String,
                        '6', '10', 'La '))
            }
            table.addRow(new YassRow('-', (page * 32 + 24) as String, '', '', ''))
        }
        table.addRow(new YassRow('E', '', '', '', ''))
        def sheet = new YassSheet()
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
        table.setRowSelectionInterval(5, 5)
        table.zoomPage()
        sheet.setPlayerPosition(sheet.beatToTimeline(table.getRowAt(6).beatInt))
        assert sheet.viewPosition.x > 100
        sheet
    }

    private static void mouseAtScrollArrow(YassSheet sheet, int eventId, boolean right) {
        int x = sheetCoordinate(sheet, right ? 'getNextSlideButtonX' : 'getPrevSlideButtonX') + 10
        int y = sheetCoordinate(sheet, 'getStickySideButtonTop') + 10
        sheet.dispatchEvent(new MouseEvent(sheet, eventId, 0,
                eventId != MouseEvent.MOUSE_RELEASED ? MouseEvent.BUTTON1_DOWN_MASK : 0,
                x, y, 1, false, MouseEvent.BUTTON1))
    }

    private static int sheetCoordinate(YassSheet sheet, String methodName) {
        def method = YassSheet.getDeclaredMethod(methodName)
        method.accessible = true
        method.invoke(sheet) as int
    }

    def 'returns no visible note when rectangles are not initialized yet'() {
        given:
        def sheet = new YassSheet()

        expect:
        sheet.firstVisibleNote() == -1
        sheet.nextNote(0) == -1
        sheet.firstVisibleNote(0) == -1
        sheet.nextNote(0, 0) == -1
    }

    def 'resize refresh skips rendering while no active table is attached'() {
        given:
        def sheet = new YassSheet()
        def viewport = new JViewport()
        viewport.setExtentSize(new Dimension(400, 240))
        viewport.setSize(400, 240)
        viewport.setView(sheet)
        sheet.setSize(400, 240)
        setPrivateField(sheet, 'image', new BufferedImage(400, 240, BufferedImage.TYPE_INT_ARGB))

        when:
        sheet.refreshImage()

        then:
        noExceptionThrown()
    }

    def 'undo restores absolute pitch window while keeping the current viewport'() {
        given:
        I18.setDefaultLanguage()

        and:
        def table = new YassTable()
        table.addRow(new YassRow(':', '0', '8', '-5', 'One '))
        table.addRow(new YassRow(':', '8', '8', '7', 'Two '))
        table.addRow(new YassRow('E', '', '', '', ''))
        def sheet = new YassSheet()
        def viewport = new JViewport()
        viewport.setExtentSize(new Dimension(1200, 420))
        viewport.setSize(1200, 420)
        viewport.setView(sheet)
        sheet.setSize(3000, 420)
        sheet.addTable(table)
        table.setSheet(sheet)
        sheet.setActiveTable(table)

        and:
        table.setRowSelectionInterval(0, 1)
        sheet.setAbsolutePitchViewEnabled(true)
        sheet.setAbsolutePitchWindow(12, 12)
        int originalWindowStart = sheet.getAbsolutePitchWindowStart()
        int originalWindowSpan = sheet.getAbsolutePitchWindowSpan()
        sheet.setViewPosition(new Point(321, (int) sheet.getViewPosition().y))
        table.addUndo()

        when:
        sheet.setAbsolutePitchWindow(0, 36)
        sheet.setViewPosition(new Point(654, (int) sheet.getViewPosition().y))
        int currentViewX = sheet.getViewPosition().x
        table.addUndo()
        table.undoRows()

        then:
        sheet.getAbsolutePitchWindowStart() == originalWindowStart
        sheet.getAbsolutePitchWindowSpan() == originalWindowSpan
        sheet.getViewPosition().x == currentViewX
    }

    def 'recording rolling mode temporarily forces absolute pitch view and restores relative view afterwards'() {
        given:
        def sheet = new YassSheet()
        sheet.enablePan(true)
        sheet.setAbsolutePitchViewEnabled(false)

        expect:
        !sheet.isAbsolutePitchViewEnabled()
        sheet.isPanEnabled()

        when:
        sheet.setRecordingRollingMode(true)

        then:
        sheet.isAbsolutePitchViewEnabled()
        sheet.isPanEnabled()

        when:
        sheet.setRecordingRollingMode(false)

        then:
        !sheet.isAbsolutePitchViewEnabled()
        sheet.isPanEnabled()
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        def field = target.class.getDeclaredField(fieldName)
        field.accessible = true
        field.set(target, value)
    }
}
