package com.zpf.support.network.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IGroup;
import com.zpf.api.IResultBean;
import com.zpf.api.OnDestroyListener;
import com.zpf.api.OnStateChangedListener;
import com.zpf.support.network.base.ErrorCode;
import com.zpf.support.network.base.ILocalCacheManager;
import com.zpf.support.network.base.INetworkCallCreator;
import com.zpf.support.network.retrofit.ResponseCallBack;

import retrofit2.Call;

public class Stevedore<T> implements OnDestroyListener, ICancelable {
    private boolean destroyed = false;
    private T resultData = null;
    private volatile boolean done = true;
    private Call<T> call;
    public INetworkCallCreator<T> callNetworkManager;
    public OnStateChangedListener<T> stateChangedListener;
    public ILocalCacheManager<T> localCacheManager;
    private volatile long lastIgnore = 0;

    protected T searchLocal() {
        if (localCacheManager == null) {
            return null;
        }
        return localCacheManager.searchLocal();
    }

    protected boolean saveToLocal(T data) {
        if (localCacheManager == null) {
            return false;
        } else {
            return localCacheManager.saveToLocal(data);
        }
    }

    protected void onStateChanged(boolean loading, int code, String msg, T data) {
        if (stateChangedListener != null) {
            stateChangedListener.onStateChanged(loading, code, msg, data);
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

    public Stevedore<T> setCallCreator(INetworkCallCreator<T> callCreator) {
        this.callNetworkManager = callCreator;
        return this;
    }

    public Stevedore<T> setStateListener(OnStateChangedListener<T> stateListener) {
        stateChangedListener = stateListener;
        return this;
    }

    public Stevedore<T> setCacheManager(ILocalCacheManager<T> cacheManager) {
        localCacheManager = cacheManager;
        return this;
    }

    public Stevedore<T> bindController(IGroup<OnDestroyListener> controller) {
        controller.add(this);
        return this;
    }

    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
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
            return;
        }
        if (!done) {
            if (!type.ignore_loading) {
                return;
            } else if (System.currentTimeMillis() - lastIgnore > 160) {
                cancel();
                lastIgnore = System.currentTimeMillis();
            } else {
                return;
            }
        }
        if (type == null) {
            type = RequestType.DEF_TYPE;
        }
        done = false;
        resultData = null;
        if (type.check_local) {
            try {
                resultData = searchLocal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (resultData != null) {
            if (type.auto_update) {
                onStateChanged(true, ErrorCode.LOAD_LOCAL_DATA, null, resultData);
                loadDataFromNetwork(type, callCreator);
            } else {
                done = true;
                onStateChanged(false, ErrorCode.LOAD_LOCAL_DATA, null, resultData);
            }
        } else {
            loadDataFromNetwork(type, callCreator);
        }
    }

    private void loadDataFromNetwork(RequestType type, @Nullable INetworkCallCreator<T> callCreator) {
        if (destroyed || type == null) {
            return;
        }
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
        onStateChanged(true, ErrorCode.LOADING_NETWORK, null, resultData);
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
                onStateChanged(false, responseResult.getCode(), null, responseResult.getData());
                call = null;
            }

            @Override
            protected void handleResponse(T response) {
                resultData = response;
                try {
                    saveToLocal(response);
                } catch (Exception e) {
                    onStateChanged(true, ErrorCode.SAVE_LOCAL_FAIL, null, response);
                }
            }
        });
    }

}
