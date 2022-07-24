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
public abstract class OkHttpRequest<R, T> extends NetRequest<R, T> {
    private Call call;
    protected INetworkCallCreator<R, Call> networkCallCreator;

    public OkHttpRequest() {
    }

    public OkHttpRequest(INetworkCallCreator<R, Call> networkCallCreator) {
        this.networkCallCreator = networkCallCreator;
    }

    public NetRequest<R, T> setCallCreator(INetworkCallCreator<R, Call> callCreator) {
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
    protected void loadDataFromNetwork(R param, int typeFlags) {
        if (destroyed || typeFlags < 0) {
            done = true;
            return;
        }
        done = false;
        if (call == null) {
            call = createNewCall(param);
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

    @Nullable
    protected Call createNewCall(R param) {
        if (networkCallCreator != null) {
            return networkCallCreator.callNetwork(param);
        } else {
            return null;
        }
    }

    public abstract T parseData(String bodyString);

}