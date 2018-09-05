package com.zpf.support.network.model;

import android.text.TextUtils;

import com.zpf.support.network.interceptor.HeaderInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ZPF on 2018/9/5.
 */
public class ClientBuilder {
    private Map<String, Object> headParams;
    private int timeOutInSeconds = 90;
    public OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    public Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

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
        return new ClientBuilder();
    }

    public ClientBuilder addInterceptor(Interceptor interceptor) {
        httpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    public ClientBuilder addNetInterceptor(Interceptor interceptor) {
        httpClientBuilder.addNetworkInterceptor(interceptor);
        return this;
    }

    public ClientBuilder setHeadParams(Map<String, Object> params) {
        headParams = params;
        return this;
    }

    public ClientBuilder addHeadParams(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (headParams == null) {
                headParams = new HashMap<>();
            }
            headParams.put(key, value);
        }
        return this;
    }

    public ClientBuilder addHeadParams(String key, int value) {
        if (value > 0) {
            if (headParams == null) {
                headParams = new HashMap<>();
            }
            headParams.put(key, value);
        }
        return this;
    }

    public ClientBuilder setTimeOut(int timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
        return this;
    }

    public ClientBuilder addCallAdapterFactory(CallAdapter.Factory factory) {
        retrofitBuilder.addCallAdapterFactory(factory);
        return this;
    }

    public ClientBuilder addConverterFactory(Converter.Factory factory) {
        retrofitBuilder.addConverterFactory(factory);
        return this;
    }

    public <T> T build(String url, Class<T> service) {
        httpClientBuilder.connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeOutInSeconds, TimeUnit.SECONDS);
        try {
            httpClientBuilder.sslSocketFactory(new TLSSocketFactory())
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (headParams != null && headParams.size() > 0) {
            httpClientBuilder.addNetworkInterceptor(new HeaderInterceptor(headParams));
        }
        retrofitBuilder.client(httpClientBuilder.build());
        retrofitBuilder.baseUrl(url);
        return retrofitBuilder.build().create(service);
    }

}
