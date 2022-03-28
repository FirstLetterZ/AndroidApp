package com.zpf.tool.network.retrofit;

import androidx.annotation.NonNull;

import com.zpf.tool.network.R;
import com.zpf.tool.network.base.BaseCallBack;
import com.zpf.tool.network.base.ErrorCode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by ZPF on 2019/2/14.
 */
public abstract class RetrofitCallBack<T> extends BaseCallBack<T> implements Callback<T> {
    private Call<T> call;

    public RetrofitCallBack() {
        super();
    }

    public RetrofitCallBack(int type) {
        super(type);
    }

    @Override
    protected void handleError(Throwable e) {
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            String description = exception.message();
            int code = exception.code();
            fail(code, description);
        } else {
            super.handleError(e);
        }
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull final Response<T> response) {
        this.call = call;
        if (call.isCanceled()) {
            return;
        }
        if (response == null) {
            onDataNull();
        } else if (response.isSuccessful()) {
            final T result = response.body();
            if (checkResponse(result)) {
                try {
                    handleResponse(result);
                } catch (Exception e) {
                    handleError(e);
                }
                runInMain(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            complete(true, responseResult);
                        } catch (Exception e) {
                            handleError(e);
                        }
                    }
                });
            } else {
                if (responseResult.getCode() == ErrorCode.RESPONSE_SUCCESS
                        || responseResult.getCode() == 0) {
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
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        this.call = call;
        if (call.isCanceled()) {
            return;
        }
        handleError(t);
    }

    @Override
    protected boolean isCancelled() {
        return call != null && call.isCanceled();
    }

    //执行网络请求
    public void requestCall(Call<T> call) {
        requestCall(call, true);
    }

    public void requestCall(Call<T> call, boolean async) {
        this.call = call;
        if (call != null && !isCancelled()) {
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

    protected void handleResponse(T response) {

    }

}