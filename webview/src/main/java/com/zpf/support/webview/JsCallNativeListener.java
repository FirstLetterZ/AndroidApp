package com.zpf.support.webview;

import com.zpf.api.dataparser.StringParseType;

/**
 * Created by ZPF on 2017/12/6.
 */
public interface JsCallNativeListener {
    Object call(@StringParseType int type, Object object, String action, String callBackName);
}
