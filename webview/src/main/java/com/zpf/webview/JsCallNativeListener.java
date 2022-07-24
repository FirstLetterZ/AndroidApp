package com.zpf.webview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.dataparser.StringParseType;

/**
 * Created by ZPF on 2017/12/6.
 */
public interface JsCallNativeListener {
    Object call(@StringParseType int type, @Nullable Object data, @NonNull String action, String callBackName);
}
