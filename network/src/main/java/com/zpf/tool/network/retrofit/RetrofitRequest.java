package com.zpf.tool.network.retrofit;

import androidx.annotation.NonNull;

import com.zpf.api.IResultBean;
import com.zpf.tool.network.base.INetworkCallCreator;
import com.zpf.tool.network.request.NetRequest;

import retrofit2.Call;

/**
 * @author Created by ZPF on 2021/3/18.
 */
public class RetrofitRequest<T> extends NetRequest<T> {
    private Call<T> call;
    protected INetworkCallCreator<Call<T>> networkCallCreator;

    public RetrofitRequest(INetworkCallCreator<Call<T>> networkCallCreator) {
        this.networkCallCreator = networkCallCreator;
    }

    public NetRequest<T> setCallCreator(INetworkCallCreator<Call<T>> callCreator) {
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
        call.enqueue(new RetrofitCallBack<T>(typeFlags) {
            @Override
            protected void complete(boolean success, @NonNull IResultBean<T> responseResult) {
                done = true;
                notifyResponse(success, responseResult.getCode(), responseResult.getData(), responseResult.getMessage());
                notifyLoading(false);
                call = null;
            }

            @Override
            protected void handleResponse(T response) {
                resultData = response;
                if (localCacheManager != null) {
                    try {
                        localCacheManager.saveToLocal(response);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        });
    }

}