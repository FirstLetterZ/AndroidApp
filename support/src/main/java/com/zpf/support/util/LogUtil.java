package com.zpf.support.util;

import android.text.TextUtils;
import android.util.Log;

import com.zpf.api.ILogger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/16.
 */
public class LogUtil implements ILogger {
    private boolean logOut = false;
    private List<ILogger> realLoggerList = new LinkedList<>();
    private static String TAG = "AppLogUtil";
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

    public static void removeLogger(ILogger realLogger) {
        get().realLoggerList.remove(realLogger);
    }

    public static void addLogger(ILogger realLogger) {
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
                if (TextUtils.isEmpty(tag)) {
                    tag = TAG;
                }
                for (ILogger logger : realLoggerList) {
                    logger.log(priority, tag, content);
                }
            } else {
                final int maxLen = 3072;
                if (content != null && content.length() > maxLen) {
                    for (int i = 0; i < content.length(); i += maxLen) {
                        if (i + maxLen < content.length())
                            Log.println(priority, tag, content.substring(i, i + maxLen));
                        else {
                            Log.println(priority, tag, content.substring(i));
                        }
                    }
                } else {
                    Log.println(priority, tag, content);
                }
            }
        }
    }
}
