package com.zpf.baselib.ui.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.zpf.baselib.cache.AppConst;
import com.zpf.baselib.interfaces.OnProgressChangedListener;
import com.zpf.baselib.util.FileUtil;
import com.zpf.baselib.util.JsonUtil;
import com.zpf.baselib.util.LogUtil;
import com.zpf.baselib.util.PublicUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/17.
 */
public class BridgeWebView extends WebView {
    private List<String> whiteList;//白名单关键字
    private int logLevel;
    private final String jsFileName = "bridge.js";
    private String jsFileString = FileUtil.assetFile2Str(getContext(), jsFileName);
    private volatile boolean waitInit = false;//页面开始加载结束，等待注入js
    private List<WebViewStateListener> stateListenerList = new ArrayList<>();
    private OnReceivedWebPageListener webPageListener;
    private WebViewWindowListener windowListener;
    private OnProgressChangedListener<WebView> progressChangedListener;
    private UrlInterceptor urlInterceptor;
    private JsCallNativeListener jsCallNativeListener;
    private String TAG = AppConst.TAG;

    public BridgeWebView(Context context) {
        super(context);
        initSetting();
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initSetting() {
        if (Build.VERSION.SDK_INT >= 19 && PublicUtil.isDebug()) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings setting = getSettings();
        setting.setJavaScriptEnabled(true);
        addJavascriptInterface(new JavaScriptInterface(), "bridge");
        setting.setBuiltInZoomControls(false);
        setting.setSupportZoom(false);
        setting.setDisplayZoomControls(false);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setAllowFileAccess(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setSavePassword(true);
        setting.setSaveFormData(true);
        setting.setGeolocationEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setDefaultTextEncodingName("UTF-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setWebChromeClient(initWebChromeClient());
        setWebViewClient(initWebViewClient());
    }

    /**
     * 创建默认的 WebChromeClient
     */
    private WebChromeClient initWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (waitInit) {
                    waitInit = !initJsBridge();
                }
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onReceivedTitle======================");
                    Log.i(TAG, "url=" + view.getUrl());
                    Log.i(TAG, "title=" + title);
                }
                if (webPageListener != null) {
                    webPageListener.onReceivedTitle(view, title);
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                if (waitInit) {
                    waitInit = !initJsBridge();
                }
                if (logLevel >= WebViewLogLevel.LEVEL_ALL) {
                    Log.i(TAG, "========================onReceivedIcon======================");
                    Log.i(TAG, "url=" + view.getUrl());
                    if (icon != null) {
                        Log.i(TAG, "icon size:width=" + icon.getWidth() + " ;height=" + icon.getHeight());
                    } else {
                        Log.i(TAG, "icon == null");
                    }
                }
                if (webPageListener != null) {
                    webPageListener.onReceivedIcon(view, icon);
                }
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onProgressChanged======================");
                    Log.i(TAG, "url=" + view.getUrl());
                    Log.i(TAG, "newProgress=" + newProgress);
                }
                if (waitInit && newProgress > 10) {
                    waitInit = !initJsBridge();
                }
                if (progressChangedListener != null) {
                    progressChangedListener.onProgressChanged(view, 100, newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onCreateWindow======================");
                    Log.i(TAG, "url=" + view.getUrl());
                }
                if (windowListener != null) {
                    return windowListener.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                } else {
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }
            }

            @Override
            public void onRequestFocus(WebView view) {
                if (windowListener != null) {
                    windowListener.onRequestFocus(view);
                }
                super.onRequestFocus(view);
            }

            @Override
            public void onCloseWindow(WebView window) {
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onCloseWindow======================");
                }
                if (windowListener != null) {
                    windowListener.onCloseWindow(window);
                }
                super.onCloseWindow(window);
            }
        };
    }

    /**
     * 创建默认的 WebViewClient
     */
    private WebViewClient initWebViewClient() {
        return new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================onPageStarted======================");
                    Log.i(TAG, "url=" + url);
                }
                if (urlInterceptor != null && urlInterceptor.check(url)) {
                    view.stopLoading();
                    goBack();
                    return;
                }
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageStart(view, url, favicon);
                    }
                }
                waitInit = true;
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================onPageFinished======================");
                    Log.i(TAG, "url=" + url);
                }
                if (urlInterceptor != null && urlInterceptor.check(url)) {
                    goBack();
                    return;
                }
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageFinish(view, url);
                    }
                }
                waitInit = false;
                super.onPageFinished(view, url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (logLevel >= WebViewLogLevel.LEVEL_ALL) {
                    Log.i(TAG, "========================doUpdateVisitedHistory======================");
                    Log.i(TAG, "url=" + url);
                    Log.i(TAG, "isReload=" + isReload);
                }
                view.clearHistory();
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================shouldOverrideUrlLoading======================");
                    Log.i(TAG, "url=" + url);
                }
                boolean intercept = urlInterceptor != null && urlInterceptor.check(url);
                if (intercept) {
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
                } else if (view.getHitTestResult() != null) {
                    view.loadUrl(url);
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
                } else if (!url.startsWith("http://") && !url.startsWith("https://")
                        && !url.startsWith("file://") && !url.equals("about:blank")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        LogUtil.w("activity not found to handle uri scheme for: " + url);
                    }
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
                } else {
                    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================shouldOverrideUrlLoading======================");
                    Log.i(TAG, "url=" + url);
                }
                boolean intercept = urlInterceptor != null && urlInterceptor.check(url);
                if (intercept) {
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
                } else if (view.getHitTestResult() != null) {
                    view.loadUrl(url);
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
                } else if (!url.startsWith("http://") && !url.startsWith("https://")
                        && !url.startsWith("file://") && !url.equals("about:blank")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        LogUtil.w("activity not found to handle uri scheme for: " + url);
                    }
                    return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
                } else {
                    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================onReceivedError======================");
                    Log.i(TAG, "request=" + JsonUtil.toString(request));
                    Log.i(TAG, "error=" + JsonUtil.toString(error));
                }
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageError(view, request, error);
                    }
                }
                waitInit = false;
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onReceivedSslError======================");
                    Log.i(TAG, "error=" + JsonUtil.toString(error));
                }
                handler.proceed(); // 接受证书
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================shouldInterceptRequest======================");
                    Log.i(TAG, "url=" + url);
                }
                boolean pass = false;
                if (whiteList != null && whiteList.size() > 0 && url != null) {
                    for (String whiteKey : whiteList) {
                        if (url.startsWith(whiteKey)) {
                            pass = true;
                            break;
                        }
                    }
                } else {
                    pass = true;
                }
                if (pass) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    return new WebResourceResponse(null, null, null);
                }
            }
        };
    }

    @SuppressLint("JavascriptInterface")
    private class JavaScriptInterface {
        @JavascriptInterface
        public void init() {
            post(new Runnable() {
                @Override
                public void run() {
                    initJsBridge();
                }
            });
        }

        @JavascriptInterface
        public String callNative(String action, String params, String callBackName) {
            if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                Log.i(TAG, "========================JavaScriptInterface callNative======================");
                Log.i(TAG, "action=" + action);
                Log.i(TAG, "params=" + params);
                Log.i(TAG, "callBackName=" + callBackName);
            }
            if (jsCallNativeListener == null) {
                return "";
            }
            Object result;
            JsonElement element = null;
            try {
                element = JsonUtil.fromJson(params, JsonElement.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (element == null) {
                if (TextUtils.isEmpty(params)) {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_NULL, null, action, callBackName);
                } else {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_STRING, params, action, callBackName);
                }
            } else if (element.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
                if (jsonPrimitive.isBoolean()) {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_BOOLEAN, jsonPrimitive.getAsBoolean(), action, callBackName);
                } else if (jsonPrimitive.isNumber()) {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_NUMBER, jsonPrimitive.getAsNumber(), action, callBackName);
                } else if (jsonPrimitive.isString()) {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_STRING, jsonPrimitive.getAsString(), action, callBackName);
                } else {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_UNKOWN, null, action, callBackName);
                }
            } else if (element.isJsonObject()) {
                try {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_JSONOBJECT, new JSONObject(params), action, callBackName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_UNKOWN, null, action, callBackName);
                }
            } else if (element.isJsonArray()) {
                try {
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_JSONARRAY, new JSONArray(params), action, callBackName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    result = jsCallNativeListener.call(JsCallNativeListener.TYPE_UNKOWN, null, action, callBackName);
                }
            } else if (element.isJsonNull()) {
                result = jsCallNativeListener.call(JsCallNativeListener.TYPE_NULL, null, action, callBackName);
            } else {
                result = jsCallNativeListener.call(JsCallNativeListener.TYPE_UNKOWN, null, action, callBackName);
            }
            if (result == null) {
                return "";
            } else {
                onCallBack(callBackName, result);
                return JsonUtil.toString(result);
            }
        }
    }

    public void onCallBack(String fucName, Object result) {
        if (fucName != null && fucName.length() > 0) {
            String forebody = "javascript:NativeCallBack('" + fucName + "'";
            if (result == null || "".equals(result)) {
                result = "";
            } else {
                if (result instanceof String) {
                    result = "'" + result + "'";
                }
                forebody = forebody + ",";
            }
            final String javascript = forebody + result + ")";
            if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                Log.i(TAG, "======================== onCallBack ======================");
                Log.i(TAG, "javascript=" + javascript);
            }
            post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        evaluateJavascript(javascript, null);
                    } else {
                        loadUrl(javascript);
                    }
                }
            });
        }
    }

    @Override
    public void goBack() {
        if (canGoBack()) {
            super.goBack();
        } else if (getContext() != null || getContext() instanceof Activity) {
            try {
                ((Activity) getContext()).finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean initJsBridge() {
        if (jsFileString != null) {
            if (logLevel >= WebViewLogLevel.LEVEL_ALL) {
                Log.i(TAG, "start load jsFile");
            }
            loadUrl("javascript:" + jsFileString);
            return true;
        } else {
            if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                Log.w(TAG, "cannot load jsFile");
            }
            return false;
        }
    }

    public void setDebug(boolean isDebug, @WebViewLogLevel int level) {
        this.logLevel = level;
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(isDebug);
        }
    }

    public void setUrlInterceptor(UrlInterceptor urlInterceptor) {
        this.urlInterceptor = urlInterceptor;
    }

    public void setWhiteList(@Nullable List<String> whiteList) {
        this.whiteList = whiteList;
    }

    @Nullable
    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setJsCallNativeListener(JsCallNativeListener listener) {
        this.jsCallNativeListener = listener;
    }

    public void addLoadStateListener(WebViewStateListener loadStateListener) {
        if (loadStateListener != null &&
                (stateListenerList.size() == 0 || !stateListenerList.contains(loadStateListener))) {
            stateListenerList.add(loadStateListener);
        }
    }

    public void setWebPageListener(OnReceivedWebPageListener webPageListener) {
        this.webPageListener = webPageListener;
    }

    public void setWindowListener(WebViewWindowListener windowListener) {
        this.windowListener = windowListener;
    }

    public void setProgressChangedListener(OnProgressChangedListener<WebView> progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }
}
