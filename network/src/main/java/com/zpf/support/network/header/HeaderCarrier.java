package com.zpf.support.network.header;

import android.text.TextUtils;

import com.zpf.support.interfaces.VariableParameterInterface;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * 头信息载体
 * Created by ZPF on 2018/9/19.
 */
public class HeaderCarrier {
    private Map<String, ClientHeader> clientHeaderMap = new HashMap<>();

    public HeaderCarrier reset(HeaderCarrier carrier) {
        if (carrier == null || carrier.getHeaderMap() == null) {
            clientHeaderMap.clear();
        } else {
            clientHeaderMap = carrier.getHeaderMap();
        }
        return this;
    }

    public HeaderCarrier resetHeaderMap(Map<String, ClientHeader> headers) {
        if (headers == null) {
            this.clientHeaderMap.clear();
        } else {
            this.clientHeaderMap.clear();
            addHeaderMap(headers);
        }
        return this;
    }

    public HeaderCarrier addHeaderMap(Map<String, ClientHeader> headers) {
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

    public HeaderCarrier addHeader(ClientHeader header) {
        if (header != null) {
            if (!TextUtils.isEmpty(header.getName())) {
                clientHeaderMap.put(header.getName(), header);
            }
        }
        return this;
    }

    public HeaderCarrier addHeader(String name, VariableParameterInterface parameterInterface) {
        if (!TextUtils.isEmpty(name) && parameterInterface != null) {
            clientHeaderMap.put(name, new VolatileHeader(name, parameterInterface));
        }
        return this;
    }

    public HeaderCarrier removeHeader(String name) {
        clientHeaderMap.remove(name);
        return this;
    }

    public Request.Builder addHeaders(Request.Builder builder) {
        if (builder != null) {
            for (Map.Entry<String, ClientHeader> entry : clientHeaderMap.entrySet()) {
                ClientHeader entryValue = entry.getValue();
                if (entryValue != null && entryValue.getValue() != null) {
                    String value = entryValue.getValue();
                    if (value != null) {
                        builder.addHeader(entry.getKey(), value);
                    }
                }
            }
        }
        return builder;
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

    public void clear() {
        clientHeaderMap.clear();
    }

    public int size() {
        return clientHeaderMap.size();
    }

    public Map<String, ClientHeader> getHeaderMap() {
        return clientHeaderMap;
    }
}