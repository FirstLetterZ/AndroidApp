package com.zpf.app.global;


import com.zpf.tool.network.header.HeaderCarrier;
import com.zpf.tool.network.util.OkHttpNetUtil;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author Created by ZPF on 2021/3/19.
 */
public final class DataCall {
    private static HashMap<Class<?>, Object> apiMap = new HashMap<>();

    public static synchronized <T> T getApi(Class<T> apiClass) {
        T result = (T) apiMap.get(apiClass);
        if (result == null) {
            OkHttpClient.Builder clientBuilder = OkHttpNetUtil.createOkHttpClientBuilder(new HeaderCarrier());
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.client(clientBuilder.build()).baseUrl("http://api.test.com");
            result = retrofitBuilder.build().create(apiClass);
            apiMap.put(apiClass, result);
        }
        return result;
    }

}