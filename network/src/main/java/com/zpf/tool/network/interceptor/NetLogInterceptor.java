package com.zpf.tool.network.interceptor;

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

    @Override
    @NonNull
    public Response intercept(@NonNull Chain chain) throws IOException {
        final OnNetLogListener netLogListener = logListener;
        if (netLogListener == null) {
            return chain.proceed(chain.request());
        }
        long t1 = System.currentTimeMillis();
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        StringBuilder builder = new StringBuilder(128);
        builder.append("{\"method\":\"").append(request.method()).append("\"");//请求类型
        builder.append(",\"url\":\"").append(httpUrl).append("\"");//请求url
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            builder.append(",\"params\":");//请求参数
            addRequestParams(builder, requestBody);
        }
        addHeads(builder, request.headers());//请求头
        builder.append("}");
        netLogListener.log(t1, true, builder.toString());
        builder.delete(0, builder.length());
        //返回数据
        builder.append("{\"url\":\"").append(httpUrl).append("\"");//请求url
        builder.append(",\"response\":");//响应数据
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            e.printStackTrace();
            builder.append("{\"error\":\"").append(e).append("\"}");
            netLogListener.log(t1, false, builder.toString());
            throw e;
        }
        String bodyString;
        long t2 = System.currentTimeMillis();
        boolean success = response.isSuccessful();
        if (success) {
            ResponseBody body = response.body();
            bodyString = Util.smartReadBodyString(body);
            if (bodyString == null || bodyString.length() == 0) {
                bodyString = "{\"code\":" + response.code() + ",\"message\":\"" + response.message() +
                        "\",\"Content-Type\":\"" + response.headers().get("Content-Type") +
                        "\",\"Content-Length\":\"" + response.headers().get("Content-Length") +
                        "\"}";
            }
            builder.append(bodyString);
        } else {
            builder.append("{\"code\":").append(response.code()).append(",\"message\":\"").append(response.message()).append("\"}");
        }
        addHeads(builder, response.headers());//响应头
        builder.append(",\"time-consuming\":").append((t2 - t1)).append("}");
        netLogListener.log(t1, success, builder.toString());
        return response;
    }

    private void addHeads(StringBuilder builder, Headers headers) {
        builder.append(",\"heads\":{");//请求头
        for (int i = 0; i < headers.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("\"").append(headers.name(i)).append("\":");
            String value = headers.value(i);
            if (value.length() == 0) {
                builder.append("\"\"");
            } else if (value.startsWith("\"")) {
                builder.append(value);
            } else {
                builder.append("\"").append(value).append("\"");
            }
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
                            if (par.length() == 0) {
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