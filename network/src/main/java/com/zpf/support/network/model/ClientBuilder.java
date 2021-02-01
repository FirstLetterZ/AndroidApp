package com.zpf.support.network.model;

import com.zpf.support.network.header.HeaderCarrier;
import com.zpf.support.network.interceptor.HeaderInterceptor;
import com.zpf.support.network.util.TrustAllUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by ZPF on 2018/9/5.
 */
public class ClientBuilder {
    private static final int timeOutInSeconds = 90;
    private final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    private final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    private final HeaderInterceptor headerInterceptor = new HeaderInterceptor();

    public static ClientBuilder createDefBuilder(HeaderCarrier headerCarrier) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.httpClientBuilder.retryOnConnectionFailure(true);
        clientBuilder.httpClientBuilder.connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeOutInSeconds, TimeUnit.SECONDS);
        TrustAllUtil.setClientTrustAll(clientBuilder.httpClientBuilder);
        clientBuilder.httpClientBuilder.addNetworkInterceptor(clientBuilder.headerInterceptor);
        clientBuilder.headerInterceptor.setHeaderCarrier(headerCarrier);
        return clientBuilder;
    }

    public static OkHttpClient.Builder createOkHttpClientBuilder(HeaderCarrier headerCarrier) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        builder.connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeOutInSeconds, TimeUnit.SECONDS);
        TrustAllUtil.setClientTrustAll(builder);
        HeaderInterceptor headerInterceptor = new HeaderInterceptor();
        headerInterceptor.setHeaderCarrier(headerCarrier);
        builder.addNetworkInterceptor(headerInterceptor);
        return builder;
    }

    public HeaderCarrier headerBuilder() {
        return headerInterceptor.getHeaderCarrier();
    }

    public Retrofit.Builder retrofitBuilder() {
        return retrofitBuilder;
    }

    public OkHttpClient.Builder clientBuilder() {
        return httpClientBuilder;
    }

    public <T> T build(String url, Class<T> service) {
        retrofitBuilder.client(httpClientBuilder.build());
        retrofitBuilder.baseUrl(url);
        return retrofitBuilder.build().create(service);
    }

}
