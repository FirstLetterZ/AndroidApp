package com.zpf.support.rxnetwork;

import android.support.annotation.Nullable;

import com.zpf.api.ICallback;
import com.zpf.api.IManager;
import com.zpf.support.network.model.HttpResult;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class RxResultCallBack<T> extends RxCallBack<HttpResult<T>> {
    public RxResultCallBack() {
        super();
    }

    public RxResultCallBack(int type) {
        super(type);
    }

    @Override
    protected final void handleResponse(HttpResult<T> response) {
        handleResult(response.getData());
    }

    @Override
    public RxResultCallBack<T> toBind(IManager<ICallback> manager) {
        super.toBind(manager);
        return this;
    }

    @Override
    protected boolean checkResultSuccess(HttpResult<T> result) {
        return (result == null || result.isSuccess());
    }

    @Override
    protected void onResultIllegal(@Nullable HttpResult<T> result) {
        if (result != null) {
            fail(result.getCode(), result.getMessage());
        } else {
            onDataNull();
        }
    }

    protected abstract void handleResult(T result);
}
