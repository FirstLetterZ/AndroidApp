package com.zpf.support.network.base;

import com.zpf.support.network.header.HeaderCarrier;
import com.zpf.support.network.model.ClientBuilder;
import com.zpf.support.network.model.RequestHelper;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ZPF on 2018/7/26.
 */
public class BaseCall {
    protected HeaderCarrier headerCarrier;

    public BaseCall(HeaderCarrier headerCarrier) {
        this.headerCarrier = headerCarrier;
    }

    public ClientBuilder builder() {
        ClientBuilder builder = ClientBuilder.createDefBuilder();
        builder.retrofitBuilder().addConverterFactory(GsonConverterFactory.create());
        builder.headerBuilder().reset(headerCarrier);
        return builder;
    }

    public RequestBody toRequestBody() {
        return RequestHelper.createJsonRequest();
    }

    public RequestBody toRequestBody(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return RequestHelper.createJsonRequest(jsonObject.toString());
    }

    public RequestBody toRequestBody(JSONObject jsonObject) {
        if (jsonObject == null) {
            return RequestHelper.createJsonRequest();
        } else {
            return RequestHelper.createJsonRequest(jsonObject.toString());
        }
    }

}