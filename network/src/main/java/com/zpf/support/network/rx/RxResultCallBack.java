package com.zpf.support.network.rx;

import com.zpf.support.network.HttpResult;

/**
 * Created by ZPF on 2018/7/26.
 */

public abstract class RxResultCallBack<T> extends RxCallBack<HttpResult<T>> {

    @Override
    public void onNext(HttpResult<T> result) {
        removeObservable();
        if (checkNull(result)) {
            fail(DATA_NULL, "返回数据为空", true);
        } else if (result == null || result.isSuccess()) {
            try {
                handleResponse(result);
                complete(true);
            } catch (Exception e) {
                handleError(e);
            }
        } else {
            fail(result.getCode(), result.getMessage(), true);
        }
    }

    @Override
    protected final void handleResponse(HttpResult<T> response) {
        handleResult(response.getData());
    }

    protected abstract void handleResult(T result);
}
