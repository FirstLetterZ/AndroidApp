package com.zpf.tool.network.request;

import com.zpf.api.ICancelable;
import com.zpf.api.IGroup;
import com.zpf.api.OnDestroyListener;
import com.zpf.tool.network.base.ErrorCode;
import com.zpf.tool.network.base.ILocalCacheManager;
import com.zpf.tool.network.base.OnLoadingListener;
import com.zpf.tool.network.base.OnResponseListener;
import com.zpf.tool.network.model.RequestType;
import com.zpf.tool.global.CentralManager;

public abstract class NetRequest<T> implements OnDestroyListener, ICancelable {
    protected boolean destroyed = false;
    protected volatile boolean done = true;
    private volatile long lastIgnore = 0;
    protected T resultData = null;
    protected OnResponseListener<T> responseListener;
    protected ILocalCacheManager<T> localCacheManager;

    @Override
    public void onDestroy() {
        destroyed = true;
        cancel();
    }

    protected void notifyLoading(final boolean loading) {
        if (responseListener instanceof OnLoadingListener) {
            CentralManager.runOnMainTread(new Runnable() {
                @Override
                public void run() {
                    if (responseListener instanceof OnLoadingListener) {
                        ((OnLoadingListener) responseListener).onLoading(loading);
                    }
                }
            });
        }
    }

    protected void notifyResponse(final boolean success, final int code, final T data, final String msg) {
        if (responseListener != null) {
            CentralManager.runOnMainTread(new Runnable() {
                @Override
                public void run() {
                    if (responseListener != null) {
                        responseListener.onResponse(success, code, data, msg);
                    }
                }
            });
        }
    }

    public T getResultData() {
        return resultData;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isEnable() {
        return !destroyed;
    }

    public NetRequest<T> setResponseListener(OnResponseListener<T> responseListener) {
        if (this.responseListener instanceof ProxyResponseListener) {
            ((ProxyResponseListener<T>) this.responseListener).realListener = responseListener;
        } else {
            this.responseListener = responseListener;
        }
        return this;
    }

    public OnResponseListener<T> getResponseListener() {
        return responseListener;
    }

    public NetRequest<T> setCacheManager(ILocalCacheManager<T> cacheManager) {
        localCacheManager = cacheManager;
        return this;
    }

    public NetRequest<T> bindController(IGroup<OnDestroyListener> controller) {
        controller.add(this);
        return this;
    }

    public void load() {
        load(RequestType.TYPE_DEFAULT);
    }

    public void load(int typeFlags) {
        if (destroyed) {
            done = true;
            return;
        }
        if (typeFlags < 0) {
            typeFlags = RequestType.TYPE_DEFAULT;
        }
        if (!done && !isCancelled()) {
            if (RequestType.checkFlag(typeFlags, RequestType.FLAG_FORCE_LOAD) && System.currentTimeMillis() - lastIgnore > 160) {
                cancel();
                lastIgnore = System.currentTimeMillis();
            } else {
                return;
            }
        }
        resultData = null;
        if (RequestType.checkFlag(typeFlags, RequestType.FLAG_USE_CACHE)) {
            if (localCacheManager != null) {
                try {
                    resultData = localCacheManager.searchLocal();
                } catch (Exception e) {
                    //
                }
            }
        }
        if (resultData != null) {
            if (RequestType.checkFlag(typeFlags, RequestType.FLAG_UPDATE_CACHE)) {
                notifyResponse(true, ErrorCode.LOAD_LOCAL_DATA, resultData, null);
                loadDataFromNetwork(typeFlags);
            } else {
                done = true;
                notifyResponse(true, ErrorCode.LOAD_LOCAL_DATA, resultData, null);
            }
        } else {
            loadDataFromNetwork(typeFlags);
        }
    }

    protected abstract void loadDataFromNetwork(int typeFlags);

}