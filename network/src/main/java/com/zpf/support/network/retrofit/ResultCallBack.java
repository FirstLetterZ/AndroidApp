package com.zpf.support.network.retrofit;

import com.zpf.api.ICallback;
import com.zpf.api.IManager;
import com.zpf.support.network.model.ResponseResult;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class ResultCallBack<T> extends ResponseCallBack<ResponseResult<T>> {
    public ResultCallBack() {
        super();
    }

    public ResultCallBack(int type) {
        super(type);
    }

    @Override
    protected void handleResponse(ResponseResult<T> response) {
        handleResult(response.getData());
    }

    @Override
    public ResultCallBack<T> toBind(IManager<ICallback> manager) {
        super.toBind(manager);
        return this;
    }

    protected abstract void handleResult(T result);
}