package com.zpf.support.interfaces;

/**
 * Created by ZPF on 2018/6/13.
 */

public interface SafeWindowInterface {
    void show();

    void dismiss();

    boolean isShowing();

    void bindController(SafeWindowController controller);

    void bindRequest(CallBackInterface callBackInterface);
}
