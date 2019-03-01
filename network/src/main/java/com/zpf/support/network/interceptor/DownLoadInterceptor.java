package com.zpf.support.network.interceptor;

import android.view.View;

import com.zpf.api.OnProgressListener;
import com.zpf.support.network.model.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 下载拦截
 * Created by ZPF on 2018/7/26.
 */
public class DownLoadInterceptor implements Interceptor {
    private OnProgressListener<View> listener;

    public DownLoadInterceptor(OnProgressListener<View> listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), listener)).build();
    }
}
