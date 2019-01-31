package com.zpf.tool.glide;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

/**
 * Created by ZPF on 2018/7/17.
 */
public class MyGlideUrl extends GlideUrl {
    private String stringUrl;

    public MyGlideUrl(URL url) {
        this(url, Headers.DEFAULT);
    }

    public MyGlideUrl(String url) {
        this(url, Headers.DEFAULT);
    }

    public MyGlideUrl(URL url, Headers headers) {
        super(url, headers);
    }

    public MyGlideUrl(String url, Headers headers) {
        super(url, headers);
        stringUrl = url;
    }

    @Override
    public String getCacheKey() {
        if (stringUrl != null) {
            if (stringUrl.split("\\?").length > 1) {
                return stringUrl.split("\\?")[0];
            } else {
                return stringUrl;
            }
        }
        return super.getCacheKey();
    }
}
