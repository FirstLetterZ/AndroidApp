package com.zpf.support.util;

import android.util.Log;

import com.zpf.api.LoggerInterface;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/16.
 */
public class LogUtil implements LoggerInterface {
    private boolean logOut = false;
    private List<LoggerInterface> realLoggerList = new LinkedList<>();
    private static String TAG = "AppLog";
    private static volatile LogUtil mInstance;

    public LogUtil() {
    }

    private static LogUtil get() {
        if (mInstance == null) {
            synchronized (LogUtil.class) {
                if (mInstance == null) {
                    mInstance = new LogUtil();
                }
            }
        }
        return mInstance;
    }

    public static void d(String content) {
        get().log(Log.DEBUG, TAG, content);
    }

    public static void i(String content) {
        get().log(Log.INFO, TAG, content);
    }

    public static void w(String content) {
        get().log(Log.WARN, TAG, content);
    }

    public static void e(String content) {
        get().log(Log.ERROR, TAG, content);
    }

    public static void setLogOut(boolean logOut) {
        get().logOut = logOut;
    }

    public static boolean isLogOut() {
        return get().logOut;
    }

    public static void removeLogger(LoggerInterface realLogger) {
        get().realLoggerList.remove(realLogger);
    }

    public static void addLogger(LoggerInterface realLogger) {
        get().realLoggerList.add(realLogger);
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        LogUtil.TAG = TAG;
    }

    @Override
    public void log(int priority, String tag, String content) {
        if (get().logOut) {
            if (realLoggerList.size() > 0) {
                for (LoggerInterface logger : realLoggerList) {
                    logger.log(priority, tag, content);
                }
            } else {
                Log.println(priority, tag, content);
            }
        }
    }
}
