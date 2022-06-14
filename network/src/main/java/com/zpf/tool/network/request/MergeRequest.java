package com.zpf.tool.network.request;

import androidx.annotation.NonNull;

import com.zpf.api.OnResultListener;
import com.zpf.tool.network.base.OnLoadingListener;
import com.zpf.tool.network.base.OnResponseListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MergeRequest {

    @interface ReceiveStrategy {
        int RECEIVE_NO_FAIL = 0;//只有全部成功才返回响应数据
        int RECEIVE_ALL_SUCCESS = 1;//返回所有成功接口的响应数据
        int RECEIVE_ALL_RESULT = -1;//返回所有接口的响应数据，不论是否成功
    }

    private final HashMap<NetRequest<?>, ProxyResponseListener<?>> calls = new HashMap<>();
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);
    private volatile boolean done = true;
    private volatile int strategy = 0;
    private OnLoadingListener loadingListener;
    private final OnLoadingListener requestLoadListener = new OnLoadingListener() {
        @Override
        public void onLoading(boolean loadingData) {
            if (loadingData) {
                requestCount.incrementAndGet();
            } else {
                if (requestCount.decrementAndGet() == 0) {
                    onComplete();
                }
            }
        }
    };
    private final OnResultListener requestResultListener = new OnResultListener() {
        @Override
        public void onResult(boolean success) {
            if (!success) {
                failCount.incrementAndGet();
            }
        }
    };

    public MergeRequest(OnLoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }

    public <T> MergeRequest merge(@NonNull NetRequest<T> netCall, int loadFlags) {
        OnResponseListener<T> responseListener = netCall.getResponseListener();
        ProxyResponseListener<T> proxyResponseListener;
        if (responseListener instanceof ProxyResponseListener) {
            proxyResponseListener = (ProxyResponseListener<T>) responseListener;
        } else {
            proxyResponseListener = new ProxyResponseListener<>(responseListener);
        }
        proxyResponseListener.loadingListener = requestLoadListener;
        proxyResponseListener.resultListener = requestResultListener;
        proxyResponseListener.loadType = loadFlags;
        netCall.setResponseListener(proxyResponseListener);
        calls.put(netCall, proxyResponseListener);
        return this;
    }

    /**
     * @param receiveStrategy @see {@link MergeRequest.ReceiveStrategy}
     */
    public void load(int receiveStrategy) {
        if (!done) {
            return;
        }
        done = calls.size() == 0;
        requestCount.set(0);
        failCount.set(0);
        this.strategy = receiveStrategy;
        if (done) {
            return;
        }
        if (loadingListener != null) {
            this.loadingListener.onLoading(true);
        }
        Iterator<Map.Entry<NetRequest<?>, ProxyResponseListener<?>>> iterator = calls.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<NetRequest<?>, ProxyResponseListener<?>> entry = iterator.next();
            NetRequest<?> netCall = entry.getKey();
            ProxyResponseListener<?> listener = entry.getValue();
            if (netCall != null && netCall.isEnable()) {
                netCall.load(listener.loadType);
            } else {
                iterator.remove();
            }
        }
        if (requestCount.get() == 0) {
            onComplete();
        }
    }

    public void cancel() {
        Iterator<Map.Entry<NetRequest<?>, ProxyResponseListener<?>>> iterator = calls.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<NetRequest<?>, ProxyResponseListener<?>> entry = iterator.next();
            NetRequest<?> netCall = entry.getKey();
            if (netCall != null && netCall.isEnable()) {
                netCall.cancel();
            } else {
                iterator.remove();
            }
        }
        onLoadFinish();
    }

    private void onComplete() {
        if (calls.size() == 0) {
            onLoadFinish();
            return;
        }
        boolean onlySuccess;
        if (failCount.get() == 0) {
            onlySuccess = true;
        } else {
            //只有全成功才发送
            if (strategy == ReceiveStrategy.RECEIVE_NO_FAIL) {
                onLoadFinish();
                return;
            }
            onlySuccess = strategy >= ReceiveStrategy.RECEIVE_ALL_SUCCESS;
        }
        Iterator<Map.Entry<NetRequest<?>, ProxyResponseListener<?>>> iterator = calls.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<NetRequest<?>, ProxyResponseListener<?>> entry = iterator.next();
            NetRequest<?> netCall = entry.getKey();
            ProxyResponseListener<?> listener = entry.getValue();
            if (netCall != null && netCall.isEnable()) {
                listener.dispatchResult(onlySuccess);
            } else {
                iterator.remove();
            }
        }
        onLoadFinish();
    }

    public int getFailCount() {
        return failCount.get();
    }

    private void onLoadFinish() {
        done = true;
        if (loadingListener != null) {
            this.loadingListener.onLoading(false);
        }
    }

    public MergeRequest setLoadingListener(OnLoadingListener loadingListener) {
        this.loadingListener = loadingListener;
        return this;
    }
}
