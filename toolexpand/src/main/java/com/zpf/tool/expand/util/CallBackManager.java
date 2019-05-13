package com.zpf.tool.expand.util;

import android.util.LongSparseArray;

import com.zpf.api.ICallback;
import com.zpf.api.IManager;
import com.zpf.api.OnDestroyListener;

/**
 * Created by ZPF on 2018/6/13.
 */
public class CallBackManager implements IManager<ICallback>, OnDestroyListener {
    private LongSparseArray<ICallback> callBackList = new LongSparseArray<>();
    private volatile boolean cancelAll = false;

    @Override
    public long bind(ICallback callBack) {
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
    public boolean execute(long id) {
        return false;
    }

    @Override
    public void remove(long id) {
        if (!cancelAll) {
            callBackList.remove(id);
        }
    }

    @Override
    public void cancel(long id) {
        if (!cancelAll) {
            ICallback callBack = callBackList.get(id);
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
                        ICallback callBack = callBackList.valueAt(i);
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
    public void reset() {
        cancelAll();
        cancelAll = false;
    }

    @Override
    public void onDestroy() {
        reset();
    }
}
