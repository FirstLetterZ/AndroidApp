package com.zpf.support.network.header;

import android.text.TextUtils;

import com.zpf.api.IKVPair;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * 头信息载体
 * Created by ZPF on 2018/9/19.
 */
public class HeaderCarrier {
    private Map<String, IKVPair<String,String>> clientHeaderMap = new HashMap<>();

    public HeaderCarrier reset(HeaderCarrier carrier) {
        if (carrier == null || carrier.getHeaderMap() == null) {
            clientHeaderMap.clear();
        } else {
            clientHeaderMap = carrier.getHeaderMap();
        }
        return this;
    }

    public HeaderCarrier resetHeaderMap(Map<String, IKVPair<String,String>> headers) {
        if (headers == null) {
            this.clientHeaderMap.clear();
        } else {
            this.clientHeaderMap.clear();
            addHeaderMap(headers);
        }
        return this;
    }

    public HeaderCarrier addHeaderMap(Map<String, IKVPair<String,String>> headers) {
        if (headers != null) {
            String entryKey;
            IKVPair<String,String> entryValue;
            for (Map.Entry<String, IKVPair<String,String>> entry : headers.entrySet()) {
                entryKey = entry.getKey();
                entryValue = entry.getValue();
                if (!TextUtils.isEmpty(entryKey) && entryValue != null) {
                    clientHeaderMap.put(entryKey, entryValue);
                }
            }
        }
        return this;
    }

    public HeaderCarrier addHeaderPair(Map<String, String> headers) {
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

    public HeaderCarrier addHeader(String name, String value) {
        if (!TextUtils.isEmpty(name) && value != null) {
            clientHeaderMap.put(name, new ConstantHeader(name, value));
        }
        return this;
    }

    public HeaderCarrier addHeader(IKVPair<String,String> header) {
        if (header != null) {
            if (!TextUtils.isEmpty(header.getKey())) {
                clientHeaderMap.put(header.getKey(), header);
            }
        }
        return this;
    }

    public HeaderCarrier removeHeader(String name) {
        clientHeaderMap.remove(name);
        return this;
    }

    public Request.Builder addHeaders(Request.Builder builder) {
        if (builder != null) {
            for (Map.Entry<String, IKVPair<String,String>> entry : clientHeaderMap.entrySet()) {
                IKVPair<String,String> entryValue = entry.getValue();
                if (entryValue != null && entryValue.getValue() != null) {
                    String value = entryValue.getValue();
                    if (value != null) {
                        builder.header(entry.getKey(), value);
                    }
                }
            }
        }
        return builder;
    }

    public Headers makeHeaders() {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, IKVPair<String,String>> entry : clientHeaderMap.entrySet()) {
            IKVPair<String,String> entryValue = entry.getValue();
            if (entryValue != null && entryValue.getValue() != null) {
                String value = entryValue.getValue();
                if (value != null) {
                    builder.add(entry.getKey(), value);
                }
            }
        }
        return builder.build();
    }

    public void clear() {
        clientHeaderMap.clear();
    }

    public int size() {
        return clientHeaderMap.size();
    }

    public Map<String, IKVPair<String,String>> getHeaderMap() {
        return clientHeaderMap;
    }
}