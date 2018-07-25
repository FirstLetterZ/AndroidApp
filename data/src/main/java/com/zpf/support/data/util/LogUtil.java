package com.zpf.support.data.util;

import android.util.Log;

import com.zpf.support.data.constant.AppConst;
import com.zpf.support.data.constant.BaseKeyConst;

/**
 * Created by ZPF on 2018/4/16.
 */
public class LogUtil {

    public static void d(String content) {
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG)) {
            Log.d(AppConst.TAG, content);
        }
    }

    public static void i(String content) {
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG)) {
            Log.i(AppConst.TAG, content);
        }
    }

    public static void w(String content) {
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG)) {
            Log.w(AppConst.TAG, content);
        }
    }

    public static void e(String content) {
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG)) {
            Log.e(AppConst.TAG, content);
        }
    }

}
