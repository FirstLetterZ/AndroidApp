package com.zpf.support.network.model;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ZPF on 2018/10/17.
 */
public class RequestHelper {

    public static RequestBody createJsonRequest(Map<String, Object> map) {
        if (map == null) {
            return createEmptyJsonRequest();
        } else {
            JSONObject jsonObject = new JSONObject(map);
            return createJsonRequest(jsonObject.toString());
        }
    }

    public static RequestBody createEmptyJsonRequest() {
        return createJsonRequest("");
    }

    public static RequestBody createJsonRequest(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            jsonString = "{}";
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonString);
    }

    public static MultipartBody createMultipartBody(Map<String, File> map) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, File> entry : map.entrySet()) {
            File value = entry.getValue();
            if (value != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), value);
                builder.addFormDataPart(entry.getKey(), value.getName(), requestBody);
            }
        }
        return builder.build();
    }

    public static MultipartBody.Part createMultipart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public static Map<String, RequestBody> createRequestMap(Map<String, Object> maps) {
        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), value.toString());
                params.put(entry.getKey(), requestBody);
            }
        }
        return params;
    }

    public static RequestBuilder buildRequest() {
        return new RealRequestBuilder();
    }

    public static RequestBuilder buildRequest(JSONObject params) {
        return new RealRequestBuilder(params);
    }
}