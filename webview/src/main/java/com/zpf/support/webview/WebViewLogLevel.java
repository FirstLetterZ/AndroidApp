package com.zpf.support.webview;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2018/7/17.
 */
@IntDef(value = {WebViewLogLevel.LEVEL_ALL, WebViewLogLevel.LEVEL_MOST,
        WebViewLogLevel.LEVEL_NORMAL, WebViewLogLevel.LEVEL_NONE,})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebViewLogLevel {
    int LEVEL_ALL = 3;
    int LEVEL_MOST = 2;
    int LEVEL_NORMAL = 1;
    int LEVEL_NONE = 0;
}
