package com.zpf.webview;

import android.graphics.Bitmap;

/**
 * Created by ZPF on 2018/7/17.
 * 详细见 WebChromeClient
 */
public interface OnReceivedWebPageListener {
    void onReceivedTitle(String title);

    void onReceivedIcon(Bitmap icon);
}
