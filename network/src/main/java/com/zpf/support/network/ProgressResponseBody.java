package com.zpf.support.network;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;

import com.zpf.support.interfaces.OnProgressChangedListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by ZPF on 2018/2/28.
 */
public class ProgressResponseBody extends ResponseBody {
    private static final int UPDATE = -1;
    private ResponseBody responseBody;
    private Handler myHandler;
    private BufferedSource bufferedSource;
    private OnProgressChangedListener<View> mListener;

    public ProgressResponseBody(ResponseBody responseBody, OnProgressChangedListener<View> listener) {
        this.responseBody = responseBody;
        mListener = listener;
        myHandler = new Handler(Looper.getMainLooper());
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
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onProgressChanged(null, bytesReaded, contentLength());
                        }
                    }
                });
                return bytesRead;
            }
        };
    }

}