package com.zpf.baselib.interfaces;

import android.app.Dialog;

import com.zpf.baselib.http.BaseCallBack;
import com.zpf.baselib.util.CallBackManager;

/**
 * Created by ZPF on 2018/6/14.
 */

public interface CallBackInterface extends OnDestroyListener {
    void cancel();

    BaseCallBack bindToManager(CallBackManager manager);

    BaseCallBack bindToManager(CallBackManager manager, Dialog dialog);
}
