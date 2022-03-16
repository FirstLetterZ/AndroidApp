package com.zpf.app.global;

import com.google.gson.Gson;
import com.zpf.support.network.header.HeaderCarrier;
import com.zpf.support.network.model.ClientBuilder;

import java.util.HashMap;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Created by ZPF on 2021/3/19.
 */
public final class DataCall {
    private static HashMap<Class<?>, Object> apiMap = new HashMap<>();

    public static synchronized <T> T getApi(Class<T> apiClass) {
        T result = (T) apiMap.get(apiClass);
        if (result == null) {
            ClientBuilder clientBuilder = ClientBuilder.createDefBuilder(new HeaderCarrier());
            clientBuilder.retrofitBuilder().addConverterFactory(GsonConverterFactory.create(new Gson()));
            result = clientBuilder.build("http://api.test.com", apiClass);
            apiMap.put(apiClass, result);
        }
        return result;
    }

}