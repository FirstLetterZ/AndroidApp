package com.zpf.tool.network.base;

import okhttp3.RequestBody;

/**
 * Created by ZPF on 2018/10/17.
 */
public interface RequestBuilder {
    RequestBuilder put(String name, Object value);

    RequestBody build();
}
