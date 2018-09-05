package com.zpf.support.network.interceptor;

import com.zpf.support.interfaces.VariableParameterInterface;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZPF on 2018/9/5.
 */
public class HeaderInterceptor implements Interceptor {
    private Map<String, Object> map;

    public HeaderInterceptor(Map<String, Object> headParams) {
        this.map = headParams;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    if (entryValue instanceof String) {
                        builder.addHeader(entry.getKey(), (String) entryValue);
                    } else if (entryValue instanceof VariableParameterInterface) {
                        Object currentValue = ((VariableParameterInterface) entryValue).getCurrentValue();
                        if (currentValue != null && currentValue instanceof String) {
                            builder.addHeader(entry.getKey(), (String) currentValue);
                        }
                    }
                }
            }
        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}
