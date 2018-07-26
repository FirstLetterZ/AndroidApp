package com.zpf.support.network.retrofit;

import com.zpf.support.generalUtil.MainHandler;
import com.zpf.support.network.HttpResult;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class NetResultCallBack<T> extends NetCallBack<HttpResult<T>> {

    @Override
    public void onResponse(Call<HttpResult<T>> call, final Response<HttpResult<T>> response) {
        if (isCancel()) {
            return;
        }
        removeObservable();
        if (response.isSuccessful()) {
            final HttpResult<T> httpResult = response.body();
            if (checkNull(httpResult)) {
                fail(DATA_NULL, "返回数据为空", true);
            } else {
                MainHandler.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (httpResult == null || httpResult.isSuccess()) {
                            try {
                                handleResponse(response.body());
                                complete(true);
                            } catch (Exception e) {
                                handleError(e);
                            }
                        } else {
                            fail(httpResult.getCode(), httpResult.getMessage(), true);
                        }
                    }
                });
            }
        } else {
            MainHandler.get().post(new Runnable() {
                @Override
                public void run() {
                    fail(response.code(), response.message(), true);
                }
            });
        }
    }

    @Override
    public void onFailure(Call<HttpResult<T>> call, final Throwable t) {
        if (isCancel()) {
            return;
        }
        MainHandler.get().post(new Runnable() {
            @Override
            public void run() {
                handleError(t);
            }
        });
    }

    @Override
    protected void handleResponse(HttpResult<T> result) {
        handleResult(result.getData());
    }

    //执行网络请求
    public void requestCall(Call<HttpResult<T>> call) {
        super.requestCall(call);
    }

    protected abstract void handleResult(T result);
}
