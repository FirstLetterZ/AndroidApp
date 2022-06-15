package com.zpf.webview;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * 监听webview加载
 * Created by ZPF on 2018/6/20.
 */
public interface WebViewStateListener {
    void onPageStart(WebView view, String url, Bitmap favicon);//开始加载

    void onPageFinish(WebView view, String url);//完成加载

    void onPageError(WebView view, String url, int code, String message);//加载失败
}
