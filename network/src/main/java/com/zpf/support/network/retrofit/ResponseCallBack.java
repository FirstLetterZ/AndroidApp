package com.zpf.support.network.retrofit;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.support.network.base.BaseCallBack;
import com.zpf.support.network.base.ErrorCode;
import com.zpf.util.network.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZPF on 2019/2/14.
 */
public abstract class ResponseCallBack<T> extends BaseCallBack<T> implements Callback<T> {
    private Call call;

    public ResponseCallBack() {
        super();
    }

    public ResponseCallBack(int type) {
        super(type);
    }

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
        if (response == null) {
            onDataNull();
        } else if (response.isSuccessful()) {
            final T result = response.body();
            if (checkResponse(result)) {
                runInMain(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handleResponse(result);
                            complete(true, responseResult);
                        } catch (Exception e) {
                            handleError(e);
                        }
                    }
                });
            } else {
                if (responseResult.getCode() == ErrorCode.RESPONSE_SUCCESS) {
                    responseResult.setCode(ErrorCode.RESPONSE_ILLEGAL);
                    responseResult.setMessage(getString(R.string.network_illegal_error));
                }
                fail(responseResult.getCode(), responseResult.getMessage());
            }
        } else {
            fail(response.code(), response.message());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (isCancel()) {
            return;
        }
        handleError(t);
    }

    @Override
    public ResponseCallBack<T> toBind(IManager<ICancelable> manager) {
        super.toBind(manager);
        return this;
    }

    //执行网络请求
    public void requestCall(Call<T> call) {
        requestCall(call, true);
    }

    public void requestCall(Call<T> call, boolean async) {
        this.call = call;
        if (call != null && !isCancel()) {
            if (async) {
                call.enqueue(this);
            } else {
                try {
                    Response<T> response = call.execute();
                    onResponse(call, response);
                } catch (Exception e) {
                    onFailure(call, e);
                }
            }
        }
    }

    protected abstract void handleResponse(T response);
}