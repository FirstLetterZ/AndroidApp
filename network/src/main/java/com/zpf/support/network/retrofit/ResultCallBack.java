package com.zpf.support.network.retrofit;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.IResultBean;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class ResultCallBack<T> extends ResponseCallBack<IResultBean<T>> {
    public ResultCallBack() {
        super();
    }

    public ResultCallBack(int type) {
        super(type);
    }

    @Override
    protected void handleResponse(IResultBean<T> response) {
        handleResult(response.getData());
    }

    @Override
    public ResultCallBack<T> toBind(IManager<ICancelable> manager) {
        super.toBind(manager);
        return this;
    }

    protected abstract void handleResult(T result);
}