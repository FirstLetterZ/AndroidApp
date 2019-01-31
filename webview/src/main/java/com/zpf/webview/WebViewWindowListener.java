package com.zpf.webview;

import android.os.Message;
import android.webkit.WebView;

/**
 * Created by ZPF on 2018/7/17.
 * 详细见 WebChromeClient
 */
public interface WebViewWindowListener {
    boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg);

    void onRequestFocus(WebView view);

    void onCloseWindow(WebView window);

}
