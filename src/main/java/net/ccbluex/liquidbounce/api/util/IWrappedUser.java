package net.ccbluex.liquidbounce.api.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author ChengFeng
 * @since 2022/11/30
 */

public interface IWrappedUser extends StdCallLibrary {
    IWrappedUser INSTANCE = Native.loadLibrary("user32", IWrappedUser.class);

    interface WNDENUMPROC extends StdCallCallback {
        boolean callback(Pointer hWnd, Pointer arg);
    }

    boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

    int GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);
}
