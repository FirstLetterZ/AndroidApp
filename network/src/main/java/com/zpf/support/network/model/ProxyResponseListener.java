package com.zpf.support.network.model;

import androidx.annotation.CallSuper;

import com.zpf.support.network.base.OnResponseListener;

/**
 * @author Created by ZPF on 2021/3/18.
 */
public abstract class ProxyResponseListener<T> implements OnResponseListener<T> {
    OnResponseListener<T> realListener;
    boolean success;
    int code;
    T data;
    String msg;
    public int loadType;

    public ProxyResponseListener(OnResponseListener<T> realListener) {
        this.realListener = realListener;
    }

    @Override
    @CallSuper
    public void onResponse(boolean success, int code, T data, String msg) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public void dispatchResult() {
        if (realListener != null) {
            realListener.onResponse(success, code, data, msg);
        }
    }
}
