package com.zpf.support.interfaces;

/**
 * Created by ZPF on 2018/6/14.
 */

public interface CallBackInterface extends com.zpf.support.interfaces.OnDestroyListener {
    void cancel();

    CallBackInterface bindToManager(CallBackManagerInterface manager);

    CallBackInterface bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog);
}
