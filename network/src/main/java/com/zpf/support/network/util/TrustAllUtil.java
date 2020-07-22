package com.zpf.support.network.util;

import androidx.annotation.Nullable;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class TrustAllUtil {

    @Nullable
    public static SSLSocketFactory getSslSocketFactory() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }

    public static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
    }

    public static TrustManager[] getTrustManager() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String message) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
    }

    public static boolean setClientTrustAll(OkHttpClient.Builder builder) {
        try {
            builder.sslSocketFactory(TrustAllUtil.getSslSocketFactory(), TrustAllUtil.getX509TrustManager())
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
