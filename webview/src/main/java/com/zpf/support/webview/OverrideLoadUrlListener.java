package com.zpf.support.webview;

/**
 * Created by ZPF on 2018/11/26.
 */
public interface OverrideLoadUrlListener {
    void overrideLoadUrl(String url,boolean isRedirect);
}
