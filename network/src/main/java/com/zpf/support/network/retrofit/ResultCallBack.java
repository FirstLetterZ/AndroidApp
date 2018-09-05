package com.zpf.support.network.retrofit;

import android.support.annotation.Nullable;

import com.zpf.support.network.model.HttpResult;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class ResultCallBack<T> extends ResponseCallBack<HttpResult<T>> {

    @Override
    protected void handleResponse(HttpResult<T> result) {
        handleResult(result.getData());
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
