package com.zpf.support.network.interceptor;

import androidx.annotation.NonNull;

import com.zpf.support.network.header.HeaderCarrier;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 全局请求头添加
 * Created by ZPF on 2018/9/5.
 */
public class HeaderInterceptor implements Interceptor {
    private final HeaderCarrier headerCarrier = new HeaderCarrier();

    @Override
    @NonNull
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (headerCarrier.size() > 0) {
            Request request = headerCarrier.addHeaders(chain.request().newBuilder())
                    .build();
            return chain.proceed(request);
        } else {
            return chain.proceed(chain.request());
        }
    }

    public void setHeaderCarrier(HeaderCarrier headerCarrier) {
        if (headerCarrier == null) {
            this.headerCarrier.clear();
        } else {
            this.headerCarrier.reset(headerCarrier);
        }
    }

    public HeaderCarrier getHeaderCarrier() {
        return headerCarrier;
    }
}
