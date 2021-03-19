package com.zpf.support.network.okhttp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.support.network.base.BaseCallBack;
import com.zpf.support.network.base.ErrorCode;
import com.zpf.support.network.util.Util;
import com.zpf.util.network.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ZPF .
 */
public abstract class OkHttpCallBack<T> extends BaseCallBack<T> implements Callback {
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
    public void onResponse(@NonNull Call call, @NonNull final Response response) {
        if (isCancelled() || call.isCanceled()) {
            return;
        }
        removeObservable();
        if (response == null) {
            onDataNull();
        } else if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body == null) {
                onDataNull();
                return;
            }
            String bodyString;
            String subType = Util.getMediaSubType(body);
            if ("json".equals(subType)) {
                try {
                    bodyString = body.string();
                } catch (IOException e) {
                    fail(ErrorCode.PARSE_ERROR, getString(R.string.network_parse_error));
                    return;
                }
            } else {
                bodyString = "{\"subType\":\"" + subType + "\"}";
            }
            T result;
            try {
                result = parseData(bodyString);
            } catch (Exception e) {
                fail(ErrorCode.PARSE_ERROR, getString(R.string.network_parse_error));
                return;
            }
            if (checkResponse(result)) {
                try {
                    handleResponse(body, result);
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
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        if (isCancelled() || call.isCanceled()) {
            return;
        }
        removeObservable();
        handleError(e);
    }

    @Override
    public OkHttpCallBack<T> toBind(IManager<ICancelable> manager) {
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

    public abstract T parseData(String bodyString);

    protected void handleResponse(@NonNull ResponseBody responseBody, @Nullable T parseData) throws Throwable {

    }

}