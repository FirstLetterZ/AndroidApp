package com.zpf.baselib.util;

import android.util.LongSparseArray;

import com.zpf.baselib.http.BaseCallBack;
import com.zpf.baselib.interfaces.OnDestroyListener;

/**
 * Created by ZPF on 2018/6/13.
 */
public class CallBackManager implements OnDestroyListener {
    private LongSparseArray<BaseCallBack> callBackList = new LongSparseArray<>();
    private volatile boolean cancelAll = false;

    public long addCallBack(BaseCallBack callBack) {
        if (cancelAll) {
            callBack.cancel();
            return -1;
        } else {
            long id = System.currentTimeMillis();
            callBackList.put(id, callBack);
            return id;
        }
    }

    public void removeCallBack(long id) {
        if (!cancelAll) {
            callBackList.remove(id);
        }
    }

    public void cancelCallBack(long id) {
        if (!cancelAll) {
            BaseCallBack callBack = callBackList.get(id);
            if (callBack != null) {
                callBack.cancel();
            }
            callBackList.remove(id);
        }
    }

    public void cancelAll() {
        synchronized (this) {
            for (int i = 0; i < callBackList.size(); i++) {
                BaseCallBack callBack = callBackList.valueAt(i);
                if (callBack != null) {
                    callBack.cancel();
                }
            }
            callBackList.clear();
        }
    }

    @Override
    public void onDestroy() {
        cancelAll();
    }
}
