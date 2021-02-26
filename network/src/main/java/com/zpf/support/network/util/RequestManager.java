package com.zpf.support.network.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IGroup;
import com.zpf.api.IResultBean;
import com.zpf.api.OnDestroyListener;
import com.zpf.support.network.base.OnResponseListener;
import com.zpf.support.network.base.ErrorCode;
import com.zpf.support.network.base.ILocalCacheManager;
import com.zpf.support.network.base.INetworkCallCreator;
import com.zpf.support.network.model.RequestType;
import com.zpf.support.network.retrofit.ResponseCallBack;
import com.zpf.tool.config.MainHandler;

import retrofit2.Call;

public class RequestManager<T> implements OnDestroyListener, ICancelable {
    private boolean destroyed = false;
    private T resultData = null;
    private volatile boolean done = true;
    private Call<T> call;
    public INetworkCallCreator<T> callNetworkManager;
    public OnResponseListener<T> responseListener;
    public ILocalCacheManager<T> localCacheManager;
    private volatile long lastIgnore = 0;

    public static <T> RequestManager<T> create(INetworkCallCreator<T> creator
            , OnResponseListener<T> listener) {
        RequestManager<T> manager = new RequestManager<T>();
        manager.setCallCreator(creator).setResponseListener(listener);
        return manager;
    }

    protected T searchLocal() {
        if (localCacheManager == null) {
            return null;
        }
        return localCacheManager.searchLocal();
    }

    protected void saveToLocal(T data) {
        if (localCacheManager != null) {
            localCacheManager.saveToLocal(data);
        }
    }

    protected void notifyLoading(final boolean loading) {
        if (responseListener != null) {
            MainHandler.runOnMainTread(new Runnable() {
                @Override
                public void run() {
                    if (responseListener != null) {
                        responseListener.onLoading(loading);
                    }
                }
            });
        }
    }

    protected void notifyResponse(final int code, final String msg, final T data) {
        if (responseListener != null) {
            MainHandler.runOnMainTread(new Runnable() {
                @Override
                public void run() {
                    if (responseListener != null) {
                        responseListener.onResponse(code, data, msg);
                    }
                }
            });
        }
    }

    protected Call<T> callNetwork() {
        if (callNetworkManager == null) {
            return null;
        }
        return callNetworkManager.callNetwork();
    }

    @Nullable
    public T getResultData() {
        return resultData;
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        cancel();
    }

    public RequestManager<T> setCallCreator(INetworkCallCreator<T> callCreator) {
        this.callNetworkManager = callCreator;
        return this;
    }

    public RequestManager<T> setResponseListener(OnResponseListener<T> responseListener) {
        this.responseListener = responseListener;
        return this;
    }

    public RequestManager<T> setCacheManager(ILocalCacheManager<T> cacheManager) {
        localCacheManager = cacheManager;
        return this;
    }

    public RequestManager<T> bindController(IGroup<OnDestroyListener> controller) {
        controller.add(this);
        return this;
    }

    public void reset() {
        destroyed = false;
    }

    public boolean isEnable() {
        return !destroyed;
    }

    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            call = null;
        }
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isCancelled() {
        return call != null && call.isCanceled();
    }

    public void load() {
        load(RequestType.DEF_TYPE, null);
    }

    public void load(RequestType type) {
        load(type, null);
    }

    public void load(@Nullable INetworkCallCreator<T> callCreator) {
        load(RequestType.DEF_TYPE, callCreator);
    }

    public void load(RequestType type, @Nullable INetworkCallCreator<T> callCreator) {
        if (destroyed) {
            done = true;
            return;
        }
        if (type == null) {
            type = RequestType.DEF_TYPE;
        }
        if (!done && call != null && !call.isCanceled()) {
            if (!type.ignore_loading) {
                return;
            } else if (System.currentTimeMillis() - lastIgnore > 160) {
                cancel();
                lastIgnore = System.currentTimeMillis();
            } else {
                return;
            }
        }
        call = null;
        resultData = null;
        if (type.check_local) {
            try {
                resultData = searchLocal();
            } catch (Exception e) {
                //
            }
        }
        if (resultData != null) {
            if (type.auto_update) {
                notifyResponse(ErrorCode.LOAD_LOCAL_DATA, null, resultData);
                loadDataFromNetwork(type, callCreator);
            } else {
                done = true;
                notifyResponse(ErrorCode.LOAD_LOCAL_DATA, null, resultData);
                notifyLoading(false);
            }
        } else {
            loadDataFromNetwork(type, callCreator);
        }
    }

    private synchronized void loadDataFromNetwork(RequestType type, @Nullable INetworkCallCreator<T> callCreator) {
        if (destroyed || type == null) {
            done = true;
            return;
        }
        done = false;
        if (callCreator != null) {
            call = callCreator.callNetwork();
        }
        if (call == null) {
            call = callNetwork();
        }
        if (call == null || destroyed) {
            done = true;
            return;
        }
        notifyLoading(true);
        int requestType = 0;
        if (!type.auto_toast) {
            requestType = (requestType | 1);
        }
        if (!type.non_null) {
            requestType = (requestType | 2);
        }
        call.enqueue(new ResponseCallBack<T>(requestType) {
            @Override
            protected void complete(boolean success, @NonNull IResultBean<T> responseResult) {
                done = true;
                notifyResponse(responseResult.getCode(), responseResult.getMessage(), responseResult.getData());
                notifyLoading(true);
                call = null;
            }

            @Override
            protected void handleResponse(T response) {
                resultData = response;
                try {
                    saveToLocal(response);
                } catch (Exception e) {
                    //
                }
            }
        });
    }

}
