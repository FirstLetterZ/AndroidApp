package com.zpf.support.view.webview;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * Created by ZPF on 2018/7/17.
 * 详细见 WebChromeClient
 */
public interface OnReceivedWebPageListener {
    void onReceivedTitle(String title);

    void onReceivedIcon(Bitmap icon);
}
