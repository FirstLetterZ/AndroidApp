package com.zpf.support.network.base;

import com.zpf.support.network.interceptor.HeaderInterceptor;
import com.zpf.support.network.model.ClientBuilder;

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
    protected ClientBuilder mBuilder;

    public BaseCall(Map<String, Object> map) {
        mBuilder = ClientBuilder.createDefBuilder();
        if (map != null && map.size() > 0) {
            mBuilder.addInterceptor(new HeaderInterceptor(map));
        }
        mBuilder.addConverterFactory(GsonConverterFactory.create());
    }

    public RequestBody getRequestBody(JSONObject params) {
        if (params == null) {
            params = new JSONObject();
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), params.toString());
    }

    public RequestBody getRequestBody(Map<String, Object> params) {
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

}
