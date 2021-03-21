package com.zpf.support.network.model;

import androidx.annotation.CallSuper;

import com.zpf.api.OnResultListener;
import com.zpf.support.network.base.OnLoadResponseListener;
import com.zpf.support.network.base.OnLoadingListener;
import com.zpf.support.network.base.OnResponseListener;

/**
 * @author Created by ZPF on 2021/3/18.
 */
final class ProxyResponseListener<T> implements OnLoadResponseListener<T> {
    OnResponseListener<T> realListener;
    OnLoadingListener loadingListener;
    OnResultListener resultListener;
    int loadType;
    private boolean success;
    private int code;
    private T data;
    private String msg;

    public ProxyResponseListener(OnResponseListener<T> realListener) {
        this.realListener = realListener;
    }

    @Override
    @CallSuper
    public void onLoading(boolean loadingData) {
        if (loadingListener != null) {
            loadingListener.onLoading(loadingData);
        }
    }

    @Override
    @CallSuper
    public void onResponse(boolean success, int code, T data, String msg) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.msg = msg;
        if (resultListener != null) {
            resultListener.onResult(success);
        }
    }

    public void dispatchResult(boolean onlySuccess) {
        if (realListener != null && (success || !onlySuccess)) {
            realListener.onResponse(success, code, data, msg);
        }
    }

}