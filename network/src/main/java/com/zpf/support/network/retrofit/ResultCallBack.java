package com.zpf.support.network.retrofit;

import com.zpf.api.CallBackManagerInterface;
import com.zpf.api.SafeWindowInterface;
import com.zpf.support.network.model.HttpResult;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class ResultCallBack<T> extends ResponseCallBack<HttpResult<T>> {
    public ResultCallBack() {
        super();
    }

    public ResultCallBack(int type) {
        super(type);
    }

    @Override
    protected boolean checkResultSuccess(HttpResult<T> result) {
        return (result == null || result.isSuccess());
    }

    @Override
    protected void onResultIllegal(HttpResult<T> result) {
        if (result != null) {
            fail(result.getCode(), result.getMessage());
        } else {
            onDataNull();
        }
    }

    @Override
    protected void handleResponse(HttpResult<T> result) {
        handleResult(result.getData());
    }

    @Override
    public ResultCallBack<T> bindToManager(CallBackManagerInterface manager) {
        super.bindToManager(manager);
        return this;
    }

    @Override
    public ResultCallBack<T> bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog) {
        super.bindToManager(manager, dialog);
        return this;
    }

    protected abstract void handleResult(T result);
}