package com.zpf.support.network.retrofit;

import com.zpf.support.generalUtil.MainHandler;
import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.network.base.BaseCallBack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class ResponseCallBack<T> extends BaseCallBack<T> implements Callback<T> {
    private Call call;

    @Override
    protected void doCancel() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    @Override
    public void onResponse(Call<T> call, final Response<T> response) {
        if (isCancel()) {
            return;
        }
        removeObservable();
        if (response.isSuccessful()) {
            final T result = response.body();
            if (checkNull(response.body())) {
                onDataNull();
            } else {
                MainHandler.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (checkResult(result)) {
                            try {
                                handleResponse(result);
                                complete(true);
                            } catch (Exception e) {
                                handleError(e);
                            }
                        } else {
                            onResultIllegal(result);
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
    public void onFailure(Call<T> call, final Throwable t) {
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
    public ResponseCallBack<T> bindToManager(CallBackManagerInterface manager) {
        super.bindToManager(manager);
        return this;
    }

    @Override
    public ResponseCallBack<T> bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog) {
        super.bindToManager(manager, dialog);
        return this;
    }

    //执行网络请求
    public void requestCall(Call<T> call) {
        this.call = call;
        if (call != null) {
            call.enqueue(this);
        }
    }

    protected abstract void handleResponse(T result);
}
