package com.zpf.support.network.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zpf.support.network.util.Util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
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
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (logListener == null) {
            return chain.proceed(chain.request());
        }
        StringBuilder resultBuilder = new StringBuilder(128);
        resultBuilder.append("{");
        long t1 = System.currentTimeMillis();
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        resultBuilder.append("\"url\":\"").append(httpUrl.toString()).append("\"");//请求url
        resultBuilder.append(",\"method\":\"").append(request.method()).append("\"");//请求类型
        //请求参数
        resultBuilder.append(",\"params\":");
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            addRequestParams(resultBuilder, requestBody);
        } else {
            int querySize = httpUrl.querySize();
            resultBuilder.append("{");
            if (querySize > 0) {
                for (int i = 0; i < querySize; i++) {
                    resultBuilder.append("\"").append(httpUrl.queryParameterName(i))
                            .append("\":\"").append(httpUrl.queryParameterValue(i)).append("\"");
                    if (i < querySize - 1) {
                        resultBuilder.append(",");
                    }
                }
            }
            resultBuilder.append("}");
        }
        //返回数据
        resultBuilder.append(",\"body\":");
        Response response = chain.proceed(request);
        String bodyString = null;
        long t2 = System.currentTimeMillis();
        if (response.isSuccessful()) {
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
            resultBuilder.append(bodyString);
            resultBuilder.append(",\"time-consuming\":").append((t2 - t1)).append("}");
            logListener.onSuccess(resultBuilder.toString());
        } else {
            resultBuilder.append("{\"code\":").append(response.code()).append(",\"message\":\"").append(response.message()).append("\"}");
            resultBuilder.append(",\"time-consuming\":").append((t2 - t1)).append("}");
            logListener.onError(resultBuilder.toString());
        }
        return response;
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
        void onSuccess(String message);

        void onError(String message);
    }

}
