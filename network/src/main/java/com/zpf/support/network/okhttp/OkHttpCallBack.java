package com.zpf.support.network.okhttp;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.support.network.base.BaseCallBack;
import com.zpf.support.network.base.ErrorCode;
import com.zpf.util.network.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ZPF .
 */
public abstract class OkHttpCallBack extends BaseCallBack<ResponseBody> implements Callback {
    private Call call;

    public OkHttpCallBack() {
        super();
    }

    public OkHttpCallBack(int type) {
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
    public void onResponse(Call call, final Response response) {
        if (isCancelled() || call.isCanceled()) {
            return;
        }
        removeObservable();
        if (response == null) {
            onDataNull();
        } else if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (checkResponse(body)) {
                try {
                    handleResponse(body);
                } catch (Throwable e) {
                    handleError(e);
                    return;
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
    public void onFailure(Call call, IOException e) {
        if (isCancelled() || call.isCanceled()) {
            return;
        }
        handleError(e);
    }

    @Override
    public OkHttpCallBack toBind(IManager<ICancelable> manager) {
        super.toBind(manager);
        return this;
    }

    //执行网络请求
    public void requestCall(Call call) {
        requestCall(call, true);
    }

    public void requestCall(Call call, boolean async) {
        this.call = call;
        if (call != null && !isCancelled()) {
            if (async) {
                call.enqueue(this);
            } else {
                try {
                    Response response = call.execute();
                    onResponse(call, response);
                } catch (Exception e) {
                    if (isCancelled() || call.isCanceled()) {
                        return;
                    }
                    handleError(e);
                }
            }
        }
    }

    //默认在子线程运行
    protected void handleResponse(ResponseBody responseBody) throws Throwable {

    }

}