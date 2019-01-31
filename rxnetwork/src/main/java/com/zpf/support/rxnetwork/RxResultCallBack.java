package com.zpf.support.rxnetwork;

import android.support.annotation.Nullable;

import com.zpf.api.CallBackManagerInterface;
import com.zpf.api.SafeWindowInterface;
import com.zpf.util.network.model.HttpResult;

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
    protected boolean checkSuccessful(@Nullable HttpResult<T> result) {
        return result != null && result.isSuccess();
    }

    @Override
    protected void onUnsuccessful(@Nullable HttpResult<T> result) {
        if (result == null) {
            onDataNull();
        } else {
            fail(result.getCode(), result.getMessage(), true);
        }
    }

    @Override
    public RxResultCallBack<T> bindToManager(CallBackManagerInterface manager) {
        super.bindToManager(manager);
        return this;
    }

    @Override
    public RxResultCallBack<T> bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog) {
        super.bindToManager(manager, dialog);
        return this;
    }

    @Override
    protected boolean checkResult(@Nullable HttpResult<T> result) {
        return (result == null || result.isSuccess());
    }

    @Override
    protected void onResultIllegal(@Nullable HttpResult<T> result) {
        if (result != null) {
            fail(result.getCode(), result.getMessage(), true);
        } else {
            onDataNull();
        }
    }

    protected abstract void handleResult(T result);
}
