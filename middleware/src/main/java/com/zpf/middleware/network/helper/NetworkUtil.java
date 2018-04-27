package com.zpf.middleware.network.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zpf.middleware.constants.KeyConst;
import com.zpf.appLib.util.CacheMapUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZPF on 2018/4/24.
 */

public class NetworkUtil {
    public static void showLoading() {

    }

    public static void showLoading(String msg) {

    }

    public static void hideLoading() {

    }

    public static Interceptor makeHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain
                        .request()
                        .newBuilder()
                        .addHeader("token", CacheMapUtil.getString(KeyConst.ACCESS_TOKEN))
                        .build();
                return chain.proceed(request);
            }
        };
    }

    public static boolean isConnected(Context contex) {
        ConnectivityManager connectivity = (ConnectivityManager) contex.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

}
