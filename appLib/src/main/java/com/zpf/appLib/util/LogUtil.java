package com.zpf.appLib.util;

import android.util.Log;

import com.zpf.appLib.constant.AppConst;

/**
 * Created by ZPF on 2018/4/16.
 */

public class LogUtil {

    public static void d(String content) {
        Log.d(AppConst.LOG_TAG, content);
    }

    public static void i(String content) {
        Log.i(AppConst.LOG_TAG, content);
    }

    public static void w(String content) {
        Log.w(AppConst.LOG_TAG, content);
    }


    public static void e(String content) {
        Log.e(AppConst.LOG_TAG, content);
    }

}
