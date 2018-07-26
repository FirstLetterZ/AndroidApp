package com.zpf.support.network;

import android.view.View;

import com.zpf.support.interfaces.OnProgressChangedListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by ZPF on 2018/7/26.
 */

public class DownLoadInterceptor implements Interceptor {
    private OnProgressChangedListener<View> listener;

    public DownLoadInterceptor(OnProgressChangedListener<View> listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), listener)).build();
    }
}
