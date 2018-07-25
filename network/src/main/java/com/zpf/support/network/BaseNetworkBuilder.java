package com.zpf.support.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zpf.baselib.cache.CacheMap;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sh01097 on 2017/9/11.
 */
public class BaseNetworkBuilder {
    public static final int NORMAL = 0;
    public static final int NO_HEADER = 1;
    public static final int WITH_LOG = 2;
    public static final int NO_INTERCEPTOR = 100;

    private Map<String, Object> headParams;
    private int timeOutInSeconds = 120;
    private int type = NORMAL;
    private OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder().retryOnConnectionFailure(true);

    public static BaseNetworkBuilder create() {
        return new BaseNetworkBuilder();
    }

    public BaseNetworkBuilder addInterceptor(Interceptor interceptor) {
        httpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    public BaseNetworkBuilder addNetInterceptor(Interceptor interceptor) {
        httpClientBuilder.addNetworkInterceptor(interceptor);
        return this;
    }

    public BaseNetworkBuilder setHeadParams(Map<String, Object> params) {
        headParams = params;
        return this;
    }

    public BaseNetworkBuilder addHeadParams(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (headParams == null) {
                headParams = new HashMap<>();
            }
            headParams.put(key, value);
        }
        return this;
    }

    public BaseNetworkBuilder addHeadParams(String key, int value) {
        if (value>0) {
            if (headParams == null) {
                headParams = new HashMap<>();
            }
            headParams.put(key, value);
        }
        return this;
    }

    public BaseNetworkBuilder setTimeOut(int timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
        return this;
    }

    public BaseNetworkBuilder setType(int type) {
        this.type = type;
        return this;
    }

    public <T> T build(String url, Class<T> service) {
        httpClientBuilder.connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeOutInSeconds, TimeUnit.SECONDS);
        try {
            httpClientBuilder.sslSocketFactory(getSSLSocketFactory())
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case NO_INTERCEPTOR:
                httpClientBuilder.interceptors().clear();
                httpClientBuilder.networkInterceptors().clear();
                break;
            case NO_HEADER:
                break;
            case WITH_LOG:
            default:
                if (headParams != null && headParams.size() > 0) {
                    httpClientBuilder.addNetworkInterceptor(makeHeaderInterceptor(headParams));
                }
                break;
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(httpClientBuilder.build());
        builder.baseUrl(url);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder.build().create(service);
    }

    private SSLSocketFactory getSSLSocketFactory() throws Exception {
        //创建一个不验证证书链的证书信任管理器。
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        }};
        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts,
                new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        return sslContext
                .getSocketFactory();
    }

    private Interceptor makeHeaderInterceptor(final Map<String, Object> map) {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                if (map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if (entry.getValue() != null) {
                            if (entry.getValue() instanceof String) {
                                builder.addHeader(entry.getKey(), (String) entry.getValue());
                            } else if (entry.getValue() instanceof Integer) {
                                builder.addHeader(entry.getKey(), CacheMap.getString((Integer) entry.getValue()));
                            }
                        }
                    }
                }
                Request request = builder.build();
                return chain.proceed(request);
            }
        };
    }

    private Interceptor makeRedirectInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl beforeUrl = request.url();
                Response response = chain.proceed(request);
                HttpUrl afterUrl = response.request().url();
                //1.根据url判断是否是重定向 302、303、307
                if (response.code() > 300 && response.code() < 310 || !beforeUrl.equals(afterUrl)) {
                    //处理两种情况 1、跨协议 2、原先不是GET请求。
                    if (!beforeUrl.scheme().equals(afterUrl.scheme()) || !request.method().equals("GET")) {
                        //重新请求
                        Request newRequest = request.newBuilder().url(response.request().url()).build();
                        response = chain.proceed(newRequest);
                    }
                }
                return response;
            }
        };
    }

}
