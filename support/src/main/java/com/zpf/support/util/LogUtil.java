package com.zpf.support.util;

import android.util.Log;

import com.zpf.support.api.LoggerInterface;

/**
 * Created by ZPF on 2018/4/16.
 */
public class LogUtil {
    private static boolean logOut = false;
    private static LoggerInterface realLogger;
    private static String TAG = "AppLog";

    public static void d(String content) {
        if (logOut) {
            if (realLogger == null) {
                Log.d(TAG, content);
            } else {
                realLogger.d(TAG, content);
            }
        }
    }

    public static void i(String content) {
        if (logOut) {
            if (realLogger == null) {
                Log.i(TAG, content);
            } else {
                realLogger.i(TAG, content);
            }
        }
    }

    public static void w(String content) {
        if (logOut) {
            if (realLogger == null) {
                Log.w(TAG, content);
            } else {
                realLogger.w(TAG, content);
            }
        }
    }

    public static void e(String content) {
        if (logOut) {
            if (realLogger == null) {
                Log.e(TAG, content);
            } else {
                realLogger.e(TAG, content);
            }
        }
    }

    public static void setLogOut(boolean logOut) {
        LogUtil.logOut = logOut;
    }

    public static boolean isLogOut() {
        return logOut;
    }

    public static LoggerInterface getRealLogger() {
        return realLogger;
    }

    public static void setRealLogger(LoggerInterface realLogger) {
        LogUtil.realLogger = realLogger;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        LogUtil.TAG = TAG;
    }
}
