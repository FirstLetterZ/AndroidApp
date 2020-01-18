package com.zpf.support.network.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.IResultBean;
import com.zpf.api.OnDataResultListener;
import com.zpf.api.OnProgressListener;
import com.zpf.support.network.base.ErrorCode;
import com.zpf.support.network.header.HeaderCarrier;
import com.zpf.support.network.interceptor.DownLoadInterceptor;
import com.zpf.support.network.okhttp.DownloadCallBack;
import com.zpf.support.network.okhttp.OkHttpCallBack;
import com.zpf.tool.config.AppContext;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.util.network.R;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpNetUtil {

    private static ConcurrentHashMap<String, CacheInfo> cacheMap = new ConcurrentHashMap<>();
    private static OkHttpClient defClient;

    public static void setDefClient(@NonNull OkHttpClient client) {
        defClient = client;
    }

    public static void initDefClient(@Nullable HeaderCarrier headerCarrier) {
        defClient = ClientBuilder.createOkHttpClientBuilder(headerCarrier).build();
    }

    public static void callNetwork(@NonNull OkHttpClient client, @NonNull Request request,
                                   @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                   @Nullable IManager<ICancelable> manager) {
        callNetwork(client, request, 0, resultListener, manager, true);
    }

    public static void callNetwork(@NonNull OkHttpClient client, @NonNull Request request, int type,
                                   @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                   @Nullable IManager<ICancelable> manager, boolean singleRequest) {
        final String cacheKey = request.method() + "=" + request.url().toString();
        CacheInfo cacheInfo = cacheMap.get(cacheKey);
        if (cacheInfo == null) {
            cacheInfo = new CacheInfo();
        }
        cacheInfo.resultListener = resultListener;
        if (singleRequest) {
            if (cacheInfo.realCall != null) {
                updateCacheController(cacheInfo, cacheKey, manager);
                return;
            }
        }
        final Call call = client.newCall(request);
        cacheInfo.realCall = call;
        updateCacheController(cacheInfo, cacheKey, manager);
        cacheMap.put(cacheKey, cacheInfo);
        call.enqueue(newCallback(cacheKey, type));
    }


    public static void requestGetCall(@NonNull String url, @Nullable Map<String, String> params,
                                      @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                      @Nullable IManager<ICancelable> manager) {
        requestGetCall(url, params, null, 0, resultListener, manager, true);
    }

    public static void requestGetCall(@NonNull String url, @Nullable Map<String, String> params,
                                      @Nullable Map<String, String> heads, int type,
                                      @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                      @Nullable IManager<ICancelable> manager, boolean singleRequest) {
        final String cacheKey = "get=" + url;
        CacheInfo cacheInfo = cacheMap.get(cacheKey);
        if (cacheInfo == null) {
            cacheInfo = new CacheInfo();
        }
        cacheInfo.resultListener = resultListener;
        if (singleRequest) {
            if (cacheInfo.realCall != null) {
                updateCacheController(cacheInfo, cacheKey, manager);
                return;
            }
        }
        if (params != null && params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for (Map.Entry<String, String> p : params.entrySet()) {
                if (i > 0) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(p.getKey())
                        .append("=")
                        .append(p.getValue());
                i++;
            }
            url = url + stringBuilder.toString();
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.get().url(url);
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> p : heads.entrySet()) {
                requestBuilder.addHeader(p.getKey(), p.getValue());
            }
        }
        if (defClient == null) {
            initDefClient(null);
        }
        if (defClient != null) {
            final Call call = defClient.newCall(requestBuilder.build());
            cacheInfo.realCall = call;
            updateCacheController(cacheInfo, cacheKey, manager);
            cacheMap.put(cacheKey, cacheInfo);
            call.enqueue(newCallback(cacheKey, type));
        }
    }

    public static void requestPostCall(@NonNull String url, @Nullable RequestBody body,
                                       @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                       @Nullable IManager<ICancelable> manager) {
        requestPostCall(url, body, null, 0, resultListener, manager, true);
    }

    public static void requestPostCall(@NonNull String url, @Nullable RequestBody body,
                                       @Nullable Map<String, String> heads, int type,
                                       @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                       @Nullable IManager<ICancelable> manager, boolean singleRequest) {
        final String cacheKey = "post=" + url;
        CacheInfo cacheInfo = cacheMap.get(cacheKey);
        if (cacheInfo == null) {
            cacheInfo = new CacheInfo();
        }
        cacheInfo.resultListener = resultListener;
        if (singleRequest) {
            if (cacheInfo.realCall != null) {
                updateCacheController(cacheInfo, cacheKey, manager);
                return;
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.post(body).url(url);
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> p : heads.entrySet()) {
                requestBuilder.addHeader(p.getKey(), p.getValue());
            }
        }
        if (defClient == null) {
            initDefClient(null);
        }
        if (defClient != null) {
            final Call call = defClient.newCall(requestBuilder.build());
            cacheInfo.realCall = call;
            updateCacheController(cacheInfo, cacheKey, manager);
            cacheMap.put(cacheKey, cacheInfo);
            call.enqueue(newCallback(cacheKey, type));
        }
    }

    public static void download(@NonNull String url, @NonNull File file, @Nullable Map<String, String> heads,
                                int type, @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                @Nullable OnProgressListener progressListener,
                                @Nullable IManager<ICancelable> manager) {

        if (!file.exists()) {
            if (resultListener != null) {
                resultListener.onResult(false,
                        new ResponseResult<String>(ErrorCode.SAVE_LOCAL_FAIL,
                                AppContext.get().getString(R.string.file_not_find)));
            }
            return;
        }
        final String cacheKey = file.getName() + "=" + url;
        CacheInfo cacheInfo = cacheMap.get(cacheKey);
        if (cacheInfo == null) {
            cacheInfo = new CacheInfo();
        }
        cacheInfo.resultListener = resultListener;
        cacheInfo.progressListener = progressListener;
        if (cacheInfo.realCall != null) {
            updateCacheController(cacheInfo, cacheKey, manager);
            return;
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.get().url(url);
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> p : heads.entrySet()) {
                requestBuilder.addHeader(p.getKey(), p.getValue());
            }
        }
        OkHttpClient client = ClientBuilder.createOkHttpClientBuilder(null)
                .addInterceptor(new DownLoadInterceptor(new OnProgressListener() {
                    @Override
                    public void onChanged(long total, long current) {
                        CacheInfo cacheInfo = cacheMap.get(cacheKey);
                        if (cacheInfo != null && cacheInfo.progressListener != null) {
                            cacheInfo.progressListener.onChanged(total, current);
                        }
                    }
                }))
                .build();
        final Call call = client.newCall(requestBuilder.build());
        cacheInfo.realCall = call;
        updateCacheController(cacheInfo, cacheKey, manager);
        cacheMap.put(cacheKey, cacheInfo);
        call.enqueue(newDownloadCallback(cacheKey, type));

    }

    private static Callback newCallback(final String key, int type) {
        return new OkHttpCallBack(type) {

            @Override
            protected void complete(boolean success, @NonNull IResultBean<String> responseResult) {
                CacheInfo cacheInfo = cacheMap.get(key);
                if (cacheInfo != null && cacheInfo.resultListener != null) {
                    cacheInfo.resultListener.onResult(success, responseResult);
                }
                cacheMap.remove(key);
            }

        };
    }


    private static Callback newDownloadCallback(final String key, int type) {
        return new OkHttpCallBack(type) {

            @Override
            protected void handleResponse(String response) {
                CacheInfo cacheInfo = cacheMap.get(key);
                if (cacheInfo != null && cacheInfo.resultListener != null) {
                    cacheInfo.resultListener.onResult(true, responseResult);
                }
            }

            @Override
            protected void complete(boolean success, @NonNull IResultBean<String> responseResult) {
                if (!success) {
                    CacheInfo cacheInfo = cacheMap.get(key);
                    if (cacheInfo != null && cacheInfo.resultListener != null) {
                        cacheInfo.resultListener.onResult(false, responseResult);
                    }
                }
                cacheMap.remove(key);
            }
        };
    }

    private static void updateCacheController(CacheInfo cacheInfo, final String key, IManager<ICancelable> manager) {
        if (manager != null) {
            if (cacheInfo.controller == null) {
                cacheInfo.controller = newCancelable(key);
            }
            manager.bind(cacheInfo.controller);
        }
    }

    private static ICancelable newCancelable(final String key) {
        return new ICancelable() {
            boolean cancel = false;

            @Override
            public void cancel() {
                cancel = true;
                CacheInfo cacheInfo = cacheMap.get(key);
                if (cacheInfo != null && cacheInfo.realCall != null) {
                    cacheInfo.realCall.cancel();
                }
                cacheMap.remove(key);
            }

            @Override
            public boolean isCancelled() {
                return cancel;
            }
        };
    }

    private static class CacheInfo {
        ICancelable controller;
        Call realCall;
        OnProgressListener progressListener;
        OnDataResultListener<IResultBean<String>> resultListener;
    }
}
