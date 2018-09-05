package com.zpf.rxnetwork;

import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.network.model.HttpResult;

/**
 * Created by ZPF on 2018/7/26.
 */

public abstract class RxResultCallBack<T> extends RxCallBack<HttpResult<T>> {

    @Override
    protected final void handleResponse(HttpResult<T> response) {
        handleResult(response.getData());
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

    protected abstract void handleResult(T result);
}
