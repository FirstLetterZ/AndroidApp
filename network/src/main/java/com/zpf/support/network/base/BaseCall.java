package com.zpf.support.network.base;

import com.zpf.support.network.header.HeaderCarrier;
import com.zpf.support.network.model.ClientBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    public static RequestBody getRequestBody() {
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), "{}");
    }

    public static RequestBody getRequestBody(JSONObject params) {
        if (params == null) {
            params = new JSONObject();
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), params.toString());
    }

    public static RequestBody getRequestBody(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        JSONObject jsonObject = new JSONObject(params);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
    }

    public static MultipartBody.Part getMultipartBody(String partName, File file) {
        // 为file建立RequestBody实例
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public static Map<String, RequestBody> mapToRequestBody(Map<String, Object> maps) {
        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue().toString());
            params.put(entry.getKey(), requestBody);
        }
        return params;
    }

    public interface RequestBuilder {
        RequestBuilder put(String name, Object value);

        RequestBody build();
    }

    public static RequestBuilder buildRequest() {
        return new RequestBuilder() {
            JSONObject params = new JSONObject();

            @Override
            public RequestBuilder put(String name, Object value) {
                try {
                    params.put(name, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return this;
            }

            @Override
            public RequestBody build() {
                return getRequestBody(params);
            }
        };
    }

}