package com.zpf.support.network.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class Util {
    private static Charset defCharset = Charset.forName("UTF-8");

    @NonNull
    public static Charset checkCharset(@Nullable MediaType type) {
        if (type == null) {
            return defCharset;
        }
        Charset charset = type.charset(defCharset);
        if (charset == null) {
            charset = defCharset;
        }
        return charset;
    }

    @Nullable
    public static String readResponseString(@Nullable ResponseBody body) {
        String bodyString = null;
        if (body != null) {
            try {
                BufferedSource source = body.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.getBuffer();
                Charset charset = Util.checkCharset(body.contentType());
                bodyString = buffer.clone().readString(charset);
            } catch (IOException e) {
                //
            }
        }
        return bodyString;
    }

    public static boolean checkMediaSubType(ResponseBody body, String subType) {
        if (body == null || subType == null) {
            return false;
        }
        MediaType responseMediaType = body.contentType();
        return (responseMediaType != null && subType.equals(responseMediaType.subtype()));
    }

    public static String getMediaSubType(ResponseBody body) {
        if (body == null) {
            return null;
        }
        MediaType responseMediaType = body.contentType();
        if (responseMediaType == null) {
            return null;
        }
        return responseMediaType.subtype();
    }
}
