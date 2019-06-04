package com.zpf.support.rxnetwork;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.support.network.model.ResponseResult;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class RxResultCallBack<T> extends RxCallBack<ResponseResult<T>> {
    public RxResultCallBack() {
        super();
    }

    public RxResultCallBack(int type) {
        super(type);
    }

    @Override
    protected final void handleResponse(ResponseResult<T> response) {
        handleResult(response.getData());
    }

    @Override
    public RxResultCallBack<T> toBind(IManager<ICancelable> manager) {
        super.toBind(manager);
        return this;
    }

    protected abstract void handleResult(T result);
}
