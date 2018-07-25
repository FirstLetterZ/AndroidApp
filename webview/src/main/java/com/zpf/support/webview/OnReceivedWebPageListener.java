package com.zpf.support.webview;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * Created by ZPF on 2018/7/17.
 * 详细见 WebChromeClient
 */
public interface OnReceivedWebPageListener {
    void onReceivedTitle(WebView view, String title);

    void onReceivedIcon(WebView view, Bitmap icon);
}
