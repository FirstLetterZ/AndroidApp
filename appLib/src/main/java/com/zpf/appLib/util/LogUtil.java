package com.zpf.appLib.util;

import android.util.Log;

import com.zpf.appLib.constant.AppConst;

/**
 * Created by ZPF on 2018/4/16.
 */
public class LogUtil {

    public static void d(String content) {
        if (AppConst.instance().isDebug()) {
            Log.d(AppConst.instance().getAppTag(), content);
        }
    }

    public static void i(String content) {
        if (AppConst.instance().isDebug()) {
            Log.i(AppConst.instance().getAppTag(), content);
        }
    }

    public static void w(String content) {
        if (AppConst.instance().isDebug()) {
            Log.w(AppConst.instance().getAppTag(), content);
        }
    }

    public static void e(String content) {
        if (AppConst.instance().isDebug()) {
            Log.e(AppConst.instance().getAppTag(), content);
        }
    }

    public static void log(String content) {
        Log.e(AppConst.instance().getAppTag(), content);
    }

}
