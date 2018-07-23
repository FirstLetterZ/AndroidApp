package com.zpf.baselib.util;

import android.util.Log;

import com.zpf.baselib.cache.AppConst;

/**
 * Created by ZPF on 2018/4/16.
 */
public class LogUtil {

    public static void d(String content) {
        if (PublicUtil.isDebug()) {
            Log.d(AppConst.TAG, content);
        }
    }

    public static void i(String content) {
        if (PublicUtil.isDebug()) {
            Log.i(AppConst.TAG, content);
        }
    }

    public static void w(String content) {
        if (PublicUtil.isDebug()) {
            Log.w(AppConst.TAG, content);
        }
    }

    public static void e(String content) {
        if (PublicUtil.isDebug()) {
            Log.e(AppConst.TAG, content);
        }
    }

}
