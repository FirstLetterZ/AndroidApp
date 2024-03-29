package com.zpf.tool.network.interceptor;

import androidx.annotation.NonNull;

import com.zpf.api.OnProgressListener;
import com.zpf.tool.network.model.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 下载拦截
 * Created by ZPF on 2018/7/26.
 */
public class DownLoadInterceptor implements Interceptor {
    private final OnProgressListener listener;

    public DownLoadInterceptor(OnProgressListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), listener)).build();
    }
}
