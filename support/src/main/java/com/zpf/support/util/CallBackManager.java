package com.zpf.support.util;

import android.util.LongSparseArray;

import com.zpf.support.api.CallBackInterface;
import com.zpf.support.api.CallBackManagerInterface;

/**
 * Created by ZPF on 2018/6/13.
 */
public class CallBackManager implements CallBackManagerInterface {
    private LongSparseArray<CallBackInterface> callBackList = new LongSparseArray<>();
    private volatile boolean cancelAll = false;

    @Override
    public long addCallBack(CallBackInterface callBack) {
        if (cancelAll) {
            callBack.cancel();
            return -1;
        } else {
            long id = System.currentTimeMillis();
            callBackList.put(id, callBack);
            return id;
        }
    }

    @Override
    public void removeCallBack(long id) {
        if (!cancelAll) {
            callBackList.remove(id);
        }
    }

    @Override
    public void cancelCallBack(long id) {
        if (!cancelAll) {
            CallBackInterface callBack = callBackList.get(id);
            if (callBack != null) {
                callBack.cancel();
            }
            callBackList.remove(id);
        }
    }

    @Override
    public void cancelAll() {
        cancelAll = true;
        if (callBackList.size() > 0) {
            synchronized (this) {
                if (callBackList.size() > 0) {
                    for (int i = 0; i < callBackList.size(); i++) {
                        CallBackInterface callBack = callBackList.valueAt(i);
                        if (callBack != null) {
                            callBack.cancel();
                        }
                    }
                    callBackList.clear();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        cancelAll();
    }
}
