package com.zpf.support.webview;

import android.support.annotation.IntRange;

/**
 * Created by ZPF on 2017/12/6.
 */

public interface JsCallNativeListener {
    int TYPE_UNKNOW = 0;
    int TYPE_NULL = 0;
    int TYPE_BOOLEAN = 1;
    int TYPE_NUMBER = 2;
    int TYPE_STRING = 3;
    int TYPE_JSONOBJECT = 4;
    int TYPE_JSONARRAY = 5;

    Object call(@IntRange(from = 0, to = 6) int type, Object object, String action, String callBackName);

}
