package com.zpf.middleware.network.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ZPF on 2018/4/23.
 */

public class RetrofitHelper {
    public static final int NORMAL = 0;
    public static final int NO_INTERCEPTOR = 1;

    public <T> T getRetrofit(String url, Class<T> service) {
        return getRetrofit(url, service, NORMAL);
    }

    public <T> T getRetrofit(String url, Class<T> service, int type) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(url);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.client(createClient(type));
        return builder.build().create(service);
    }

    private OkHttpClient createClient(int type) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        try {
            addSSLSocketFactory(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case NO_INTERCEPTOR:
                break;
            default:
                builder.addInterceptor(NetworkUtil.makeHeaderInterceptor());
                break;
        }
        return builder.build();
    }

    private void addSSLSocketFactory(OkHttpClient.Builder builder) throws Exception {
        addSSLSocketFactory(builder, null, false);
    }

    private void addSSLSocketFactory(OkHttpClient.Builder builder, String filePath, boolean fromAssets) throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager trustManager = null;
        if (filePath == null) {
            trustManager = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }
            };
        } else {
            InputStream inputStream = null;
            if (fromAssets) {
//                inputStream = mContext.getAssets().open(filePath); // 得到证书的输入流
            } else {
                File file = new File(filePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                }
            }
            if (inputStream != null) {
                trustManager = trustManagerForCertificates(inputStream);//以流的方式读入证书
                inputStream.close();
            }
        }
        if (trustManager != null) {
            sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());
        }
    }

    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }
        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // 这里添加自定义的密码，默认
            keyStore.load(null, password);// By convention, 'null' creates an empty key store.
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
