package com.zpf.support.network.model;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;

/**
 * Created by ZPF on 2018/10/17.
 */
public class RealRequestBuilder implements RequestBuilder {
    private final JSONObject params;

    public RealRequestBuilder() {
        params = new JSONObject();
    }

    public RealRequestBuilder(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.params = jsonObject;
        } else {
            this.params = new JSONObject();
        }
    }

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
        return RequestHelper.createJsonRequest(params.toString());
    }
}
