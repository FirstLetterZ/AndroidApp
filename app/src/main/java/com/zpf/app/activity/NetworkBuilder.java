package com.zpf.app.activity;

import android.text.TextUtils;

import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sh01097 on 2017/9/11.
 */
public class NetworkBuilder {
    private Map<String, String> headParams;
    private int timeOutInSeconds = 120;
    private OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder().retryOnConnectionFailure(true);
    private SSLSocketFactory sslSocketFactory;

    public static NetworkBuilder create() {
        return new NetworkBuilder();
    }

    public NetworkBuilder addInterceptor(Interceptor interceptor) {
        httpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    public NetworkBuilder addNetInterceptor(Interceptor interceptor) {
        httpClientBuilder.addNetworkInterceptor(interceptor);
        return this;
    }

    public NetworkBuilder setHeadParams(Map<String, String> params) {
        headParams = params;
        return this;
    }

    public NetworkBuilder addHeadParams(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (headParams == null) {
                headParams = new HashMap<>();
            }
            headParams.put(key, value);
        }
        return this;
    }

    public NetworkBuilder addSslSocketFactory(SSLSocketFactory factory) {
        sslSocketFactory = factory;
        return this;
    }

    public NetworkBuilder setTimeOut(int timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
        return this;
    }

    public <T> T build(String url, Class<T> service) {
        httpClientBuilder.connectTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeOutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeOutInSeconds, TimeUnit.SECONDS);
        try {
            if (sslSocketFactory == null) {
                sslSocketFactory = getSSLSocketFactory();
            }
            httpClientBuilder.sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            e.printStackTrace();
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
}
