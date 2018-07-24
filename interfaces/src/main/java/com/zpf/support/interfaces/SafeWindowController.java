package com.zpf.support.interfaces;

/**
 * Created by ZPF on 2018/6/13.
 */

public interface SafeWindowController extends OnDestroyListener {
    boolean showNext();

    void show(SafeWindowInterface windowInterface);

    boolean isShowing(SafeWindowInterface windowInterface);

    boolean dismiss();
}
