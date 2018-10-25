package com.zpf.tool.glideutil;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Fetches an {@link InputStream} using the okhttp library.
 */
public class OkHttpStreamFetcher implements DataFetcher<InputStream> {
    private final OkHttpClient client;
    private final GlideUrl url;
    private InputStream stream;
    private ResponseBody responseBody;

    public OkHttpStreamFetcher(OkHttpClient client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url.toStringUrl());
        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            callback.onLoadFailed(new NullPointerException("Request failed response is null"));
        } else if (!response.isSuccessful()) {
            callback.onLoadFailed(new IOException("Request failed with code: " + response.code()));
        } else {
            responseBody = response.body();
            if (responseBody == null) {
                callback.onLoadFailed(new NullPointerException("ResponseBody is empty with code: " + response.code()));
            } else {
                stream = responseBody.byteStream();
                callback.onDataReady(stream);
            }
        }
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignored
            }
        }
        if (responseBody != null) {
            try {
                responseBody.close();
            } catch (Exception e) {
                // Ignored.
            }
        }
    }

    @Override
    public void cancel() {
        //后台继续加载
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

}
