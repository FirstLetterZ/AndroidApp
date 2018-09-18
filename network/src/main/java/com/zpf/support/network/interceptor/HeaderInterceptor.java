package com.zpf.support.network.interceptor;

import android.text.TextUtils;

import com.zpf.support.interfaces.VariableParameterInterface;
import com.zpf.support.network.header.ClientHeader;
import com.zpf.support.network.header.ConstantHeader;
import com.zpf.support.network.header.VolatileHeader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 全局请求头添加
 * Created by ZPF on 2018/9/5.
 */
public class HeaderInterceptor implements Interceptor {
    private Map<String, ClientHeader> clientHeaderMap = new HashMap<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Headers headers = makeHeaders();
        if (headers.size() > 0) {
            Request.Builder builder = chain.request().newBuilder();
            builder.headers(makeHeaders());
            Request request = builder.build();
            return chain.proceed(request);
        } else {
            return chain.proceed(chain.request());
        }
    }

    public HeaderInterceptor resetHeaderMap(Map<String, ClientHeader> headers) {
        if (headers == null) {
            this.clientHeaderMap.clear();
        } else {
            this.clientHeaderMap = headers;
        }
        return this;
    }

    public HeaderInterceptor addHeaderMap(Map<String, ClientHeader> headers) {
        if (headers != null) {
            String entryKey;
            ClientHeader entryValue;
            for (Map.Entry<String, ClientHeader> entry : headers.entrySet()) {
                entryKey = entry.getKey();
                entryValue = entry.getValue();
                if (!TextUtils.isEmpty(entryKey) && entryValue != null) {
                    clientHeaderMap.put(entryKey, entryValue);
                }
            }
        }
        return this;
    }

    public HeaderInterceptor addHeaderPair(Map<String, String> headers) {
        if (headers != null) {
            String entryKey;
            String entryValue;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                entryKey = entry.getKey();
                entryValue = entry.getValue();
                if (!TextUtils.isEmpty(entryKey) && !TextUtils.isEmpty(entryValue)) {
                    clientHeaderMap.put(entryKey, new ConstantHeader(entryKey, entryValue));
                }
            }
        }
        return this;
    }

    public HeaderInterceptor addHeader(String name, String value) {
        if (!TextUtils.isEmpty(name) && value != null) {
            clientHeaderMap.put(name, new ConstantHeader(name, value));
        }
        return this;
    }

    public HeaderInterceptor addHeader(ClientHeader header) {
        if (header != null) {
            if (!TextUtils.isEmpty(header.getName())) {
                clientHeaderMap.put(header.getName(), header);
            }
        }
        return this;
    }

    public HeaderInterceptor addHeader(String name, VariableParameterInterface parameterInterface) {
        if (!TextUtils.isEmpty(name) && parameterInterface != null) {
            clientHeaderMap.put(name, new VolatileHeader(name, parameterInterface));
        }
        return this;
    }

    public HeaderInterceptor removeHeader(String name) {
        clientHeaderMap.remove(name);
        return this;
    }

    public Headers makeHeaders() {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, ClientHeader> entry : clientHeaderMap.entrySet()) {
            ClientHeader entryValue = entry.getValue();
            if (entryValue != null && entryValue.getValue() != null) {
                String value = entryValue.getValue();
                if (value != null) {
                    builder.add(entry.getKey(), value);
                }
            }
        }
        return builder.build();
    }

}
