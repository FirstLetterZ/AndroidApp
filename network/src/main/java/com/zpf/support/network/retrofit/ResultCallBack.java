package com.zpf.support.network.retrofit;

import android.support.annotation.Nullable;

import com.zpf.support.network.model.HttpResult;

import retrofit2.Response;

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
    protected void handleResponse(HttpResult<T> result) {
        handleResult(result.getData());
    }

    @Override
    protected boolean checkSuccessful(Response<HttpResult<T>> response) {
        if (super.checkSuccessful(response)) {
            HttpResult<T> httpResult = response.body();
            return httpResult != null && httpResult.isSuccess();
        }
        return false;
    }

    @Override
    protected void onUnsuccessful(@Nullable Response<HttpResult<T>> response) {
        if (response == null) {
            onDataNull();
        } else {
            HttpResult<T> httpResult = response.body();
            if (httpResult == null) {
                onDataNull();
            } else {
                fail(httpResult.getCode(), httpResult.getMessage());
            }
        }
    }

    @Override
    protected boolean checkResult(@Nullable HttpResult<T> result) {
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
