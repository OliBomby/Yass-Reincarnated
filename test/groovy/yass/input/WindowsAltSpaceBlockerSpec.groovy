package yass.input

import com.sun.jna.CallbackReference
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.platform.win32.WinUser
import spock.lang.Requires
import spock.lang.Specification

import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.JTextField
import java.awt.GraphicsEnvironment
import java.awt.event.KeyEvent

@Requires({ Platform.isWindows() && !GraphicsEnvironment.isHeadless() })
class WindowsAltSpaceBlockerSpec extends Specification {
    def 'Alt menu activation is blocked while other system commands are forwarded and restored'() {
        given:
        def frame = new JFrame('Alt Space shortcut test')
        HWND hwnd
        Pointer original
        WindowsAltSpaceBlocker blocker
        def forwarded = []
        WinUser.WindowProc probe = { HWND window, int message, WPARAM wParam, LPARAM lParam ->
            if (message == WinUser.WM_SYSCOMMAND) {
                forwarded << [wParam.intValue(), lParam.longValue()]
                return new LRESULT(73)
            }
            User32.INSTANCE.CallWindowProc(original, window, message, wParam, lParam)
        }
        Pointer probePointer = CallbackReference.getFunctionPointer(probe)

        when:
        // Recreate the peer as fullscreen transitions do, and check both peers.
        2.times { int pass ->
            SwingUtilities.invokeAndWait {
                frame.undecorated = pass == 1
                frame.pack()
                hwnd = new HWND(Native.getWindowPointer(frame))
                original = currentProcedure(hwnd)
                setProcedure(hwnd, probePointer)
                blocker = WindowsAltSpaceBlocker.install(frame)
            }

            assert sendCommand(hwnd, 0xF100, 32) == 0
            assert sendCommand(hwnd, 0xF101, 32) == 0
            3.times {
                assert sendCommand(hwnd, 0xF100, 0) == 0
            }
            assert forwarded.empty
            assert sendCommand(hwnd, 0xF100, (int) 'f'.charAt(0)) == 73
            assert sendCommand(hwnd, 0xF090, 32) == 73
            assert forwarded.size() == 2

            SwingUtilities.invokeAndWait {
                def editor = new JTextField()
                frame.add(editor)
                3.times {
                    [KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED].each { int id ->
                        def event = new KeyEvent(editor, id, 0, 0, KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED)
                        assert blocker.dispatchAlt(event)
                        assert event.consumed
                    }
                }
                def shortcut = new KeyEvent(editor, KeyEvent.KEY_PRESSED, 0,
                        KeyEvent.ALT_DOWN_MASK, KeyEvent.VK_F, (char) 'f')
                assert !blocker.dispatchAlt(shortcut)
                assert !shortcut.consumed
                def otherWindow = new JFrame()
                try {
                    def event = new KeyEvent(otherWindow, KeyEvent.KEY_PRESSED, 0, 0,
                            KeyEvent.VK_ALT, KeyEvent.CHAR_UNDEFINED)
                    assert !blocker.dispatchAlt(event)
                    assert !event.consumed
                } finally {
                    otherWindow.dispose()
                }
            }

            SwingUtilities.invokeAndWait {
                blocker.close()
                blocker = null
            }
            assert currentProcedure(hwnd) == probePointer
            assert sendCommand(hwnd, 0xF100, 32) == 73
            SwingUtilities.invokeAndWait {
                setProcedure(hwnd, original)
                frame.dispose()
            }
            forwarded.clear()
        }

        then:
        !frame.displayable

        cleanup:
        SwingUtilities.invokeAndWait {
            blocker?.close()
            if (frame.displayable && original != null) {
                setProcedure(hwnd, original)
            }
            frame.dispose()
        }
    }

    private static long sendCommand(HWND hwnd, int command, long character) {
        User32.INSTANCE.SendMessage(hwnd, WinUser.WM_SYSCOMMAND,
                new WPARAM(command), new LPARAM(character)).longValue()
    }

    private static Pointer currentProcedure(HWND hwnd) {
        Native.POINTER_SIZE == 8
                ? User32.INSTANCE.GetWindowLongPtr(hwnd, WinUser.GWL_WNDPROC).toPointer()
                : Pointer.createConstant(Integer.toUnsignedLong(User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_WNDPROC)))
    }

    private static void setProcedure(HWND hwnd, Pointer procedure) {
        if (Native.POINTER_SIZE == 8) {
            User32.INSTANCE.SetWindowLongPtr(hwnd, WinUser.GWL_WNDPROC, procedure)
        } else {
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_WNDPROC, (int) Pointer.nativeValue(procedure))
        }
    }
}
