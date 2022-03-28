package com.zpf.tool.network.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IResultBean;
import com.zpf.tool.network.model.OkHttpCallBack;
import com.zpf.tool.network.base.INetworkCallCreator;

import okhttp3.Call;
import okhttp3.ResponseBody;

/**
 * @author Created by ZPF on 2021/3/18.
 */
public abstract class OkHttpRequest<T> extends NetRequest<T> {
    private Call call;
    protected INetworkCallCreator<Call> networkCallCreator;

    public OkHttpRequest(INetworkCallCreator<Call> networkCallCreator) {
        this.networkCallCreator = networkCallCreator;
    }

    public NetRequest<T> setCallCreator(INetworkCallCreator<Call> callCreator) {
        this.networkCallCreator = callCreator;
        return this;
    }

    @Override
    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            call = null;
        }
        done = true;
    }

    @Override
    public boolean isCancelled() {
        return destroyed || call != null && call.isCanceled();
    }

    @Override
    protected void loadDataFromNetwork(int typeFlags) {
        if (destroyed || typeFlags < 0) {
            done = true;
            return;
        }
        done = false;
        if (call == null && networkCallCreator != null) {
            call = networkCallCreator.callNetwork();
        }
        if (call == null || destroyed) {
            done = true;
            return;
        }
        notifyLoading(true);
        call.enqueue(new OkHttpCallBack<T>(typeFlags) {

            @Override
            public T parseData(String bodyString) {
                return OkHttpRequest.this.parseData(bodyString);
            }

            @Override
            protected void complete(boolean success, @NonNull IResultBean<T> responseResult) {
                done = true;
                notifyResponse(success, responseResult.getCode(), responseResult.getData(), responseResult.getMessage());
                notifyLoading(false);
                call = null;
            }

            @Override
            protected void handleResponse(@NonNull ResponseBody responseBody, @Nullable T parseData) throws Throwable {
                resultData = parseData;
                if (localCacheManager != null) {
                    try {
                        localCacheManager.saveToLocal(parseData);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        });
    }

    public abstract T parseData(String bodyString);

}