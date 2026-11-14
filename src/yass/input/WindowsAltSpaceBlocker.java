package yass.input;

import com.sun.jna.CallbackReference;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.SwingUtilities;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.Window;
import java.awt.event.KeyEvent;

/** Prevents Alt from moving focus into Swing menus or the native window chrome. */
public final class WindowsAltSpaceBlocker implements AutoCloseable {
    private static final int SC_KEYMENU = 0xF100;
    private final HWND window;
    private final Pointer originalProcedure;
    // Retain the callback for as long as Windows can call its native pointer.
    private final WinUser.WindowProc procedure;
    private final Window owner;
    private final KeyboardFocusManager focusManager;
    private final KeyEventDispatcher altDispatcher = this::dispatchAlt;

    public static WindowsAltSpaceBlocker install(Window window) {
        return Platform.isWindows() ? new WindowsAltSpaceBlocker(window) : null;
    }

    private WindowsAltSpaceBlocker(Window window) {
        owner = window;
        focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        this.window = new HWND(Native.getWindowPointer(window));
        originalProcedure = Native.POINTER_SIZE == 8
                ? User32.INSTANCE.GetWindowLongPtr(this.window, WinUser.GWL_WNDPROC).toPointer()
                : Pointer.createConstant(Integer.toUnsignedLong(
                        User32.INSTANCE.GetWindowLong(this.window, WinUser.GWL_WNDPROC)));
        procedure = (hwnd, message, wParam, lParam) -> {
            if (message == WinUser.WM_SYSCOMMAND
                    && (wParam.intValue() & 0xFFF0) == SC_KEYMENU
                    && (lParam.longValue() == ' ' || lParam.longValue() == 0)) {
                return new LRESULT(0);
            }
            return User32.INSTANCE.CallWindowProc(originalProcedure, hwnd, message, wParam, lParam);
        };
        replaceProcedure(CallbackReference.getFunctionPointer(procedure));
        focusManager.addKeyEventDispatcher(altDispatcher);
    }

    boolean dispatchAlt(KeyEvent event) {
        // Consume only the modifier itself: Alt combinations still carry their modifiers.
        if (event.getKeyCode() == KeyEvent.VK_ALT
                && (event.getComponent() == owner
                || SwingUtilities.getWindowAncestor(event.getComponent()) == owner)) {
            event.consume();
            return true;
        }
        return false;
    }

    private void replaceProcedure(Pointer replacement) {
        Native.setLastError(0);
        long previous = Native.POINTER_SIZE == 8
                ? Pointer.nativeValue(User32.INSTANCE.SetWindowLongPtr(window, WinUser.GWL_WNDPROC, replacement))
                : User32.INSTANCE.SetWindowLong(window, WinUser.GWL_WNDPROC, (int) Pointer.nativeValue(replacement));
        if (previous == 0 && Native.getLastError() != 0) {
            throw new Win32Exception(Native.getLastError());
        }
    }

    @Override
    public void close() {
        replaceProcedure(originalProcedure);
        focusManager.removeKeyEventDispatcher(altDispatcher);
    }
}
