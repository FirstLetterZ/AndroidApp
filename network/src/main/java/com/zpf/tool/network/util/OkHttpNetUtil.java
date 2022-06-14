package com.zpf.tool.network.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.IResultBean;
import com.zpf.api.OnDataResultListener;
import com.zpf.api.OnProgressListener;
import com.zpf.tool.network.model.OkHttpCallBack;
import com.zpf.tool.network.base.ErrorCode;
import com.zpf.tool.network.header.HeaderCarrier;
import com.zpf.tool.network.interceptor.DownLoadInterceptor;
import com.zpf.tool.network.interceptor.HeaderInterceptor;
import com.zpf.tool.network.interceptor.NetLogInterceptor;
import com.zpf.tool.network.model.ResponseResult;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.network.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class OkHttpNetUtil {

    private static final ConcurrentHashMap<String, CacheInfo> cacheMap = new ConcurrentHashMap<>();
    private static OkHttpClient defClient;

    public static void setDefClient(@NonNull OkHttpClient client) {
        defClient = client;
    }

    public static void initDefClient(@Nullable HeaderCarrier headerCarrier) {
        defClient = createOkHttpClientBuilder(headerCarrier).build();
    }

    public static <T> void callNetwork(@NonNull OkHttpClient client, @NonNull Request request,
                                       @Nullable OnDataResultListener<IResultBean<String>> resultListener,
                                       @Nullable IManager<ICancelable> manager) {
        callNetwork(client, request, 0, resultListener, manager, true);
    }

    public static <T> void callNetwork(@NonNull OkHttpClient client, @NonNull Request request, int type,
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
                } else {
                    stringBuilder.append("?");
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
        if (body != null) {
            requestBuilder.post(body);
        }
        requestBuilder.url(url);
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
                                Util.getString(R.string.file_not_find)));
            }
            return;
        }
        final String cacheKey = file.getAbsolutePath();
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
        OkHttpClient client = createOkHttpClientBuilder(null)
                .addNetworkInterceptor(new DownLoadInterceptor(new OnProgressListener() {
                    @Override
                    public void onProgress(long total, long current, @Nullable Object target) {
                        CacheInfo cacheInfo = cacheMap.get(cacheKey);
                        if (cacheInfo != null && cacheInfo.progressListener != null) {
                            cacheInfo.progressListener.onProgress(total, current, target);
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
        return new OkHttpCallBack<String>(type) {

            @Override
            public String parseData(String bodyString) {
                return bodyString;
            }

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
        return new OkHttpCallBack<String>(type) {
            @Override
            public String parseData(String bodyString) {
                return bodyString;
            }

            @Override
            protected void handleResponse(@NonNull ResponseBody responseBody, String parseData) throws Throwable {
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                InputStream inputStream = responseBody.byteStream();
                try {
                    fos = new FileOutputStream(key);
                    while ((len = inputStream.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        fos.flush();
                    }
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

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

    public static OkHttpClient.Builder createOkHttpClientBuilder(HeaderCarrier headerCarrier) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        TrustAllUtil.setClientTrustAll(builder);
        HeaderInterceptor headerInterceptor = new HeaderInterceptor();
        headerInterceptor.setHeaderCarrier(headerCarrier);
        builder.addNetworkInterceptor(headerInterceptor);
        if (CentralManager.isDebug()) {
            builder.addNetworkInterceptor(new NetLogInterceptor());
        }
        return builder;
    }
}