package com.zpf.webview;

class WebPageInfo {
    String webUrl;//
    boolean waitInit;//初始化完成
    boolean loadComplete;//加载完成
    long finishTime;//加载完成的时间戳
    boolean isRedirect;//重定向
}
