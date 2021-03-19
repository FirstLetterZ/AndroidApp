package com.zpf.support.network.util;

import com.zpf.support.network.base.OnResponseListener;
import com.zpf.support.network.base.INetworkCallCreator;
import com.zpf.support.network.model.NetCall;
import com.zpf.support.network.model.ProxyResponseListener;
import com.zpf.support.network.retrofit.RetrofitNetCall;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestManager {

    public static <T> RetrofitNetCall<T> create(INetworkCallCreator<retrofit2.Call<T>> creator
            , OnResponseListener<T> listener) {
        RetrofitNetCall<T> retrofitCall = new RetrofitNetCall<T>();
        retrofitCall.setCallCreator(creator).setResponseListener(listener);
        return retrofitCall;
    }

    private final HashMap<NetCall<?>, Cache<?>> calls = new HashMap<>();
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);

    public <T> void merge(NetCall<T> netCall, int loadFlags) {
        final Cache<T> cache = new Cache<>(loadFlags);
        OnResponseListener<T> responseListener = netCall.getResponseListener();
        if(!(responseListener instanceof ProxyResponseListener)){
            netCall.setResponseListener(new ProxyResponseListener<T>(responseListener) {
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

                @Override
                public void onResponse(boolean success, int code, T data, String msg) {
                    super.onResponse(success, code, data, msg);
                    if (!success) {
                        failCount.incrementAndGet();
                    }
                }
            });
        }
        calls.put(netCall, cache);
    }

    public void startLoad() {
        failCount.set(0);
        requestCount.set(0);
        for (Map.Entry<NetCall<?>, Cache<?>> entry : calls.entrySet()) {
            NetCall<?> netCall = entry.getKey();
            final Cache<?> cache = entry.getValue();
            if (netCall != null && netCall.isEnable()) {
                netCall.load(cache.loadType);
            }
        }
        if (requestCount.decrementAndGet() == 0) {
            onComplete();
        }
    }

    public void onComplete() {
        for (Map.Entry<NetCall<?>, Cache<?>> entry : calls.entrySet()) {
            final Cache<?> cache = entry.getValue();
            cache.postResult();
        }
    }

    static class Cache<T> {
        int loadType;
        boolean success;
        int code;
        T data;
        String msg;
        OnResponseListener<T> listener;

        public Cache(int loadType) {
            this.loadType = loadType;
        }

        public void postResult() {
            if (listener != null) {
                listener.onResponse(success, code, data, msg);
            }
        }
    }

}
