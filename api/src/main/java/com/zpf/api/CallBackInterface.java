package com.zpf.api;

/**
 * Created by ZPF on 2018/6/14.
 */

public interface CallBackInterface extends OnDestroyListener {
    void cancel();

    CallBackInterface bindToManager(CallBackManagerInterface manager);

    CallBackInterface bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog);
}
