package com.zpf.tool.network.model;
import androidx.annotation.NonNull;

import com.zpf.api.OnProgressListener;
import com.zpf.tool.global.MainHandler;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 下载进度监听
 * Created by ZPF on 2018/8/28.
 */
public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;
    private final OnProgressListener mListener;

    public ProgressResponseBody(ResponseBody responseBody, OnProgressListener listener) {
        this.responseBody = responseBody;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    @NonNull
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                MainHandler.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onProgress(contentLength(), bytesReaded, null);
                        }
                    }
                });
                return bytesRead;
            }
        };
    }

}