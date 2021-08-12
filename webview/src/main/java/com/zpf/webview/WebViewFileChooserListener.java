package com.zpf.webview;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * 监听webview自带的文件安选择器
 * Created by ZPF on 2018/6/20.
 */
public interface WebViewFileChooserListener {
    boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, String[] acceptTypes);
}
