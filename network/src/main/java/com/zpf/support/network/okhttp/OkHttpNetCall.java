package com.zpf.support.network.okhttp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IResultBean;
import com.zpf.support.network.base.INetworkCallCreator;
import com.zpf.support.network.model.NetCall;

import okhttp3.Call;
import okhttp3.ResponseBody;

/**
 * @author Created by ZPF on 2021/3/18.
 */
public class OkHttpNetCall extends NetCall<String> {
    private Call call;
    protected INetworkCallCreator<Call> networkCallCreator;

    public NetCall<String> setCallCreator(INetworkCallCreator<Call> callCreator) {
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
        return call != null && call.isCanceled();
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
        call.enqueue(new OkHttpCallBack<String>(typeFlags) {

            @Override
            public String parseData(String bodyString) {
                return bodyString;
            }

            @Override
            protected void complete(boolean success, @NonNull IResultBean<String> responseResult) {
                done = true;
                notifyResponse(success, responseResult.getCode(), responseResult.getData(), responseResult.getMessage());
                notifyLoading(false);
                call = null;
            }

            @Override
            protected void handleResponse(@NonNull ResponseBody responseBody, @Nullable String parseData) throws Throwable {
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

}