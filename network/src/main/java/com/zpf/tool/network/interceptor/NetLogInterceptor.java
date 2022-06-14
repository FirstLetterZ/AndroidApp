package com.zpf.tool.network.interceptor;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.zpf.tool.network.util.Util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 网络日志拦截，目前只拦截打印json类型数据内容，其余情况只打印类型
 * Created by ZPF on 2018/9/17.
 */
public class NetLogInterceptor implements Interceptor {
    private OnNetLogListener logListener;
    private final StringBuilder requestBuilder = new StringBuilder(128);
    private final StringBuilder responseBuilder = new StringBuilder(128);

    @Override
    @NonNull
    public Response intercept(@NonNull Chain chain) throws IOException {
        final OnNetLogListener netLogListener = logListener;
        if (netLogListener == null) {
            return chain.proceed(chain.request());
        }
        requestBuilder.delete(0, requestBuilder.length());
        requestBuilder.append("{");
        long t1 = System.currentTimeMillis();
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        requestBuilder.append("\"url\":\"").append(httpUrl).append("\"");//请求url
        requestBuilder.append(",\"method\":\"").append(request.method()).append("\"");//请求类型
        requestBuilder.append(",");
        addHeads(requestBuilder, request.headers());//请求头
        requestBuilder.append(",\"params\":");//请求参数
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            addRequestParams(requestBuilder, requestBody);
        } else {
            int querySize = httpUrl.querySize();
            requestBuilder.append("{");
            if (querySize > 0) {
                for (int i = 0; i < querySize; i++) {
                    requestBuilder.append("\"").append(httpUrl.queryParameterName(i))
                            .append("\":\"").append(httpUrl.queryParameterValue(i)).append("\"");
                    if (i < querySize - 1) {
                        requestBuilder.append(",");
                    }
                }
            }
            requestBuilder.append("}");
        }
        requestBuilder.append("}");
        netLogListener.log(t1, true, requestBuilder.toString());
        responseBuilder.delete(0, responseBuilder.length());
        //返回数据
        responseBuilder.append("{\"url\":\"").append(httpUrl).append("\"");//请求url
        responseBuilder.append(",\"response\":");//响应数据
        Response response = chain.proceed(request);
        String bodyString = null;
        long t2 = System.currentTimeMillis();
        boolean success = response.isSuccessful();
        if (success) {
            ResponseBody body = response.body();
            if ("json".equals(Util.getMediaSubType(body))) {
                bodyString = Util.readResponseString(body);
            }
            if (TextUtils.isEmpty(bodyString)) {
                bodyString = "{\"code\":" + response.code() + ",\"message\":\"" + response.message() +
                        "\",\"Content-Type\":\"" + response.headers().get("Content-Type") +
                        "\",\"Content-Length\":\"" + response.headers().get("Content-Length") +
                        "\"}";
            }
            responseBuilder.append(bodyString);
        } else {
            responseBuilder.append("{\"code\":").append(response.code()).append(",\"message\":\"").append(response.message()).append("\"}");
        }
        requestBuilder.append(",");
        addHeads(responseBuilder, response.headers());//请求头
        responseBuilder.append(",\"time-consuming\":").append((t2 - t1)).append("}");
        netLogListener.log(t1, success, responseBuilder.toString());
        return response;
    }

    private void addHeads(StringBuilder builder, Headers headers) {
        builder.append("\"heads\":{");//请求头
        for (int i = 0; i < headers.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("\"").
                    append(headers.name(i))
                    .append("\":\"")
                    .append(headers.value(i))
                    .append("\"");
        }
        builder.append("}");
    }

    private void addRequestParams(StringBuilder builder, RequestBody requestBody) {
        if (requestBody == null) {
            builder.append("{}");
        } else if (requestBody instanceof FormBody) {
            int size = ((FormBody) requestBody).size();
            builder.append("{");
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    builder.append("\"").append(((FormBody) requestBody).name(i))
                            .append("\":\"").append(((FormBody) requestBody).value(i)).append("\"");
                    if (i < size - 1) {
                        builder.append(",");
                    }
                }
            }
            builder.append("}");
        } else if (requestBody instanceof MultipartBody) {
            int size = ((MultipartBody) requestBody).size();
            builder.append("[");
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    addRequestParams(builder, ((MultipartBody) requestBody).part(i).body());
                }
            }
            builder.append("]");
        } else {
            long contentLength;
            try {
                contentLength = requestBody.contentLength();
            } catch (IOException e) {
                contentLength = -1;
            }
            if (contentLength > 5 * 1024) {
                builder.append("{\"contentLength\":").append(contentLength).append("}");
            } else {
                if (contentLength >= 0) {
                    Charset charset = Util.checkCharset(requestBody.contentType());
                    Buffer buffer = new Buffer();
                    try {
                        requestBody.writeTo(buffer);
                        if (buffer.size() > 0 && isPlaintext(buffer)) {
                            String par = buffer.readString(charset);
                            if (TextUtils.isEmpty(par)) {
                                builder.append("{}");
                            } else if (par.startsWith("{") || par.startsWith("[")) {
                                builder.append(par);
                            } else {
                                builder.append("\"").append(par).append("\"");
                            }
                        } else {
                            builder.append("{}");
                        }
                    } catch (IOException e) {
                        builder.append("{}");
                    }
                } else {
                    builder.append("{}");
                }
            }
        }
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

    public OnNetLogListener getLogListener() {
        return logListener;
    }

    public void setLogListener(OnNetLogListener logListener) {
        this.logListener = logListener;
    }

    public interface OnNetLogListener {
        void log(long id, boolean success, String message);
    }

}