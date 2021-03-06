package com.zpf.tool.expand.util;

import android.util.LongSparseArray;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;

/**
 * Created by ZPF on 2018/6/13.
 */
public class CancelableManager implements IManager<ICancelable> {
    private final LongSparseArray<ICancelable> callBackList = new LongSparseArray<>();
    private volatile boolean cancelAll = false;

    @Override
    public long bind(ICancelable callBack) {
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
            ICancelable callBack = callBackList.get(id);
            if (callBack != null) {
                callBack.cancel();
            }
            callBackList.remove(id);
        }
    }

    @Override
    public void reset() {
        cancelAll = true;
        clear();
        cancelAll = false;
    }

    @Override
    public void onDestroy() {
        clear();
        cancelAll = true;
    }

    private void clear() {
        if (callBackList.size() > 0) {
            synchronized (this) {
                if (callBackList.size() > 0) {
                    for (int i = 0; i < callBackList.size(); i++) {
                        ICancelable callBack = callBackList.valueAt(i);
                        if (callBack != null) {
                            callBack.cancel();
                        }
                    }
                    callBackList.clear();
                }
            }
        }
    }
}
