package com.zpf.support.network.interceptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 网络日志拦截，目前只拦截打印json类型数据内容，其余情况只打印类型
 * Created by ZPF on 2018/9/17.
 */
public class NetLogInterceptor implements Interceptor {
    private Charset defCharset = Charset.forName("UTF-8");
    private OnNetLogListener logListener;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (logListener == null) {
            return chain.proceed(chain.request());
        }
        long t1 = System.currentTimeMillis();
        Request request = chain.request();
        HttpUrl httpUrl = request.url();//请求url
        String url = httpUrl.toString();
        String method = request.method();  //请求类型
        //请求参数
        String params = null;
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            try {
                params = getRequestParams(requestBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int querySize = httpUrl.querySize();
            if (querySize > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{");
                for (int i = 0; i < querySize; i++) {
                    stringBuilder.append("\"").append(httpUrl.queryParameterName(i))
                            .append("\":\"").append(httpUrl.queryParameterValue(i)).append("\"");
                    if (i < querySize - 1) {
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.append("}");
                params = stringBuilder.toString();
            }
        }
        if (TextUtils.isEmpty(params)) {
            params = "{}";
        }
        //返回数据
        Response response = chain.proceed(request);
        String bodyString = null;
        long t2 = System.currentTimeMillis();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body != null) {
                MediaType responseMediaType = body.contentType();
                if (responseMediaType != null && "json".equals(responseMediaType.subtype())) {
                    BufferedSource source = body.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();
                    Charset charset = checkCharset(responseMediaType);
                    bodyString = buffer.clone().readString(charset);
                }
            }
            if (TextUtils.isEmpty(bodyString)) {
                bodyString = "{\"code\":" + response.code() + ",\"message\":\"" + response.message() +
                        "\",\"Content-Type\":\"" + response.headers().get("Content-Type") +
                        "\",\"Content-Length\":\"" + response.headers().get("Content-Length") +
                        "\"}";
            }
            String msg = buildLogMessage(url, method, t1, t2, params, bodyString);
            logListener.onSuccess(msg);
        } else {
            String errorMsg = "{\"code\":" + response.code() + ",\"message\":\"" + response.message() + "\"}";
            String msg = buildLogMessage(url, method, t1, t2, params, errorMsg);
            logListener.onError(msg);
        }
        return response;
    }

    private String getRequestParams(RequestBody requestBody) throws IOException {
        if (requestBody == null) {
            return "{}";
        }
        String params = null;
        if (requestBody instanceof FormBody) {
            int size = ((FormBody) requestBody).size();
            if (size > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{");
                for (int i = 0; i < size; i++) {
                    stringBuilder.append("\"").append(((FormBody) requestBody).name(i))
                            .append("\":\"").append(((FormBody) requestBody).value(i)).append("\"");
                    if (i < size - 1) {
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.append("}");
                params = stringBuilder.toString();
            }
        } else if (requestBody instanceof MultipartBody) {
            int size = ((MultipartBody) requestBody).size();
            if (size > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("[");
                for (int i = 0; i < size; i++) {
                    getRequestParams(((MultipartBody) requestBody).part(i).body());
                }
                stringBuilder.append("]");
                params = stringBuilder.toString();
            }
        } else {
            long contentLength = requestBody.contentLength();
            if (contentLength > 5 * 1024) {
                params = "{\"contentLength\":" + contentLength + "}";
            } else {
                Charset charset = checkCharset(requestBody.contentType());
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                if (isPlaintext(buffer)) {
                    params = buffer.readString(charset);
                }
            }
        }
        if (TextUtils.isEmpty(params)) {
            params = "{}";
        }
        return params;
    }

    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    @NonNull
    private Charset checkCharset(@Nullable MediaType type) {
        if (type == null) {
            return defCharset;
        }
        Charset charset = type.charset(defCharset);
        if (charset == null) {
            charset = defCharset;
        }
        return charset;
    }

    /**
     * 创建一个json格式的日志信息
     */
    private String buildLogMessage(String url, String method, long startTime, long endTime, String params, String bodyString) {
        long time = endTime - startTime;
        return "{\"url\":\"" + url +
                "\",\"method\":\"" + method +
                "\",\"time\":" + time +
                ",\"params\":" + params +
                ",\"body\":" + bodyString + "}";
    }

    public OnNetLogListener getLogListener() {
        return logListener;
    }

    public void setLogListener(OnNetLogListener logListener) {
        this.logListener = logListener;
    }

    public interface OnNetLogListener {
        void onSuccess(String message);

        void onError(String message);
    }

}
