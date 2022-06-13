package com.zpf.tool.expand.util;

import android.text.TextUtils;
import android.util.Log;

import com.zpf.api.ILogger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/16.
 */
public class Logger implements ILogger {
    private boolean logOut = false;
    private final List<ILogger> realLoggerList = new LinkedList<>();
    private int logPriority = -99;
    private static String TAG = "AppLogUtil";
    private static volatile Logger mInstance;
    private static final int MAX_LEN = 2000;

    private Logger() {
    }

    public static Logger get() {
        if (mInstance == null) {
            synchronized (Logger.class) {
                if (mInstance == null) {
                    mInstance = new Logger();
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

    public static void setLogPriority(int logPriority) {
        get().logPriority = logPriority;
    }

    public static int getLogPriority() {
        return get().logPriority;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        Logger.TAG = TAG;
    }

    @Override
    public void log(int priority, String tag, String content) {
        int realPriority = priority;
        if (priority > Log.ASSERT) {
            realPriority = Math.max(Log.VERBOSE, priority % (Log.ASSERT + 1));
        }
        if (get().logOut && realPriority >= logPriority && content != null) {
            if (realLoggerList.size() > 0) {
                if (TextUtils.isEmpty(tag)) {
                    tag = TAG;
                }
                for (ILogger logger : realLoggerList) {
                    logger.log(realPriority, tag, content);
                }
            } else {
                if (content.length() > MAX_LEN) {
                    for (int i = 0; i < content.length(); i += MAX_LEN) {
                        if (i + MAX_LEN < content.length())
                            Log.println(realPriority, tag, content.substring(i, i + MAX_LEN));
                        else {
                            Log.println(realPriority, tag, content.substring(i));
                        }
                    }
                } else {
                    Log.println(realPriority, tag, content);
                }
            }
        }
    }
}
