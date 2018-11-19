package com.zpf.support.api;

import android.support.annotation.IntRange;
import android.util.Log;

/**
 * Created by ZPF on 2018/10/26.
 */
public interface LoggerInterface {
    void log(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int priority, String tag, String content);
}
