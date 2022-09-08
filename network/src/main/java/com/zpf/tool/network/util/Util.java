package com.zpf.tool.network.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.tool.global.CentralManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class Util {
    private static final Charset defCharset = StandardCharsets.UTF_8;
    private static final long MAX_READ = 1024 * 1024;

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
                Charset charset = checkCharset(body.contentType());
                bodyString = buffer.clone().readString(charset);
            } catch (IOException e) {
                //
            }
        }
        return bodyString;
    }

    @Nullable
    public static String smartReadBodyString(ResponseBody body) {
        if (body == null) {
            return null;
        }
        if (body.contentLength() >= MAX_READ) {
            return null;
        }
        MediaType type = body.contentType();
        String typeString = null;
        if (type != null) {
            typeString = type.toString().toLowerCase();
        }
        if (typeString != null && !typeString.contains("json") && !typeString.contains("text")) {
            return null;
        }
        String bodyString = null;
        try {
            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.getBuffer();
            Charset charset = checkCharset(body.contentType());
            bodyString = buffer.clone().readString(charset);
        } catch (IOException e) {
            //
        }
        return bodyString;
    }

    public static String getString(int id) {
        try {
            return CentralManager.getAppContext().getString(id);
        } catch (Exception e) {
            return "";
        }
    }

}