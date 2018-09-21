package com.zpf.support.network.model;

import com.zpf.support.network.header.HeaderCarrier;
import com.zpf.support.network.interceptor.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by ZPF on 2018/9/5.
 */
public class ClientBuilder {
    private final int timeOutInSeconds = 90;
    private OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    private Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    private HeaderInterceptor headerInterceptor = new HeaderInterceptor();

    public static ClientBuilder createDefBuilder() {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.httpClientBuilder.retryOnConnectionFailure(true);
        clientBuilder.httpClientBuilder.connectTimeout(clientBuilder.timeOutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(clientBuilder.timeOutInSeconds, TimeUnit.SECONDS)
                .readTimeout(clientBuilder.timeOutInSeconds, TimeUnit.SECONDS);
        try {
            clientBuilder.httpClientBuilder.sslSocketFactory(new TLSSocketFactory())
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientBuilder.httpClientBuilder.addNetworkInterceptor(clientBuilder.headerInterceptor);
        return clientBuilder;
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
