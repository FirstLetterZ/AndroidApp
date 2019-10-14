package com.zpf.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zpf.api.IChecker;
import com.zpf.api.OnProgressListener;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.api.dataparser.StringParseResult;
import com.zpf.api.dataparser.StringParseType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZPF on 2018/7/25.
 */
public class BridgeWebView extends WebView {
    private List<String> whiteList;//白名单关键字
    private int logLevel;
    private final String jsFileName = "bridge.js";
    private String jsFileString;
    private List<WebViewStateListener> stateListenerList = new ArrayList<>();
    private OnReceivedWebPageListener webPageListener;
    private WebViewWindowListener windowListener;
    private WebViewFileChooserListener fileChooserListener;
    private OnProgressListener<WebView> progressChangedListener;
    private IChecker<String> urlInterceptor;//url拦截
    private JsCallNativeListener jsCallNativeListener;//js调用native的回调
    private OverrideLoadUrlListener overrideLoadUrlListener;//OverrideLoadUrlListener
    private String TAG = "BridgeWebView";
    private boolean isTraceless;//无痕浏览
    private boolean useWebTitle = true;//使用浏览器标题
    private WebPageInfo webPageInfo = new WebPageInfo();//当前加载页面
    private HashMap<String, Boolean> redirectedUrlMap = new HashMap<>();//重定向原始url集合
    private JsonParserInterface realParser;//json解析
    private View customView = null;
    private WebChromeClient.CustomViewCallback customCallback = null;

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initSetting();
        setWebChromeClient(initWebChromeClient());
        setWebViewClient(initWebViewClient());
        initJsString();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void initSetting() {
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
        setting.setSaveFormData(true);
        setting.setGeolocationEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setAppCacheEnabled(true);
        setting.setDefaultTextEncodingName("UTF-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    protected void initJsString() {
        InputStream in = null;
        try {
            in = getContext().getAssets().open(jsFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (in != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder sb = new StringBuilder();
                do {
                    line = bufferedReader.readLine();
                    if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                        sb.append(line);
                    }
                } while (line != null);
                bufferedReader.close();
                in.close();
                jsFileString = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        if (jsFileString == null) {
            jsFileString = "function CallNative(name, body, callBack) {\n" +
                    "    if (!name) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    var id = \"\";\n" +
                    "    var param;\n" +
                    "    if(!body){\n" +
                    "        param = \"\";\n" +
                    "    }else if(typeof body === 'string'){\n" +
                    "        param = body;\n" +
                    "    }else{\n" +
                    "        param = JSON.stringify(body);\n" +
                    "    }\n" +
                    "    if(callBack && typeof callBack === 'function'){\n" +
                    "        var date = new Date();\n" +
                    "        var id = date.toISOString();\n" +
                    "        var newAction = {\n" +
                    "            id: id,\n" +
                    "            callBack: callBack,\n" +
                    "        };\n" +
                    "        Native.actions.push(newAction);\n" +
                    "    }\n" +
                    "   return window.bridge.callNative(name,param,id);\n" +
                    "};\n" +
                    "function NativeCallBack(id, body) {\n" +
                    "    if (!id) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    if (!Native.actions) {\n" +
                    "        return;\n" +
                    "    }\n" +
                    "    for (var index in Native.actions) {\n" +
                    "        var action = Native.actions[index];\n" +
                    "        if (action.id === id) {\n" +
                    "            var callBack = action.callBack;\n" +
                    "            if (callBack && typeof callBack === 'function') {\n" +
                    "                if (body) {\n" +
                    "                    callBack(body)\n" +
                    "                } else {\n" +
                    "                    callBack()\n" +
                    "                }\n" +
                    "            }\n" +
                    "            break;\n" +
                    "        }\n" +
                    "    }\n" +
                    "};\n" +
                    "if (!Native) {\n" +
                    "    var Native = window.Native ={\n" +
                    "        actions : [],\n" +
                    "        jsCall : CallNative\n" +
                    "    };\n" +
                    "    window.bridge.connect();\n" +
                    "}";
        }
    }

    /**
     * 创建默认的 WebChromeClient
     */
    protected WebChromeClient initWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (webPageInfo.waitInit) {
                    webPageInfo.waitInit = !initJsBridge();
                }
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onReceivedTitle======================");
                    Log.i(TAG, "url=" + view.getUrl());
                    Log.i(TAG, "title=" + title);
                }
                if (webPageListener != null) {
                    webPageListener.onReceivedTitle(title);
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                if (webPageInfo.waitInit) {
                    webPageInfo.waitInit = !initJsBridge();
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
                    webPageListener.onReceivedIcon(icon);
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
                if (webPageInfo.waitInit && newProgress > 10) {
                    initJsBridge();
                }
                if (progressChangedListener != null) {
                    progressChangedListener.onChanged(view, 100, newProgress);
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

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                customView = view;
                customCallback = callback;
                ViewGroup parent = (ViewGroup) getParent();
                parent.removeView(BridgeWebView.this);
                parent.addView(view);
            }

            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                customView = view;
                customCallback = callback;
                ViewGroup parent = (ViewGroup) getParent();
                parent.removeView(BridgeWebView.this);
                parent.addView(view);
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) {
                    return;
                }
                // 全屏返回之后，视频状态不能衔接上，因为onCustomViewHidden很多情况下会奔溃
                //原因： Call back (only in API level <19, because in API level 19+ with chromium webview it crashes)
                if (customCallback != null) {
                    if (customCallback.getClass().getName().contains(".chromium.")) {
                        customCallback.onCustomViewHidden();
                    }
                }
                ViewGroup parent = (ViewGroup) customView.getParent();
                parent.removeView(customView);
                parent.addView(BridgeWebView.this);
                customCallback = null;
                customView = null;
            }

            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (fileChooserListener != null) {
                    return fileChooserListener.onShowFileChooser(webView, filePathCallback,
                            fileChooserParams.getAcceptTypes());
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                if (fileChooserListener != null) {
                    final ValueCallback<Uri> valueCallback = uploadMsg;
                    ValueCallback<Uri[]> filePathCallback = new ValueCallback<Uri[]>() {
                        @Override
                        public void onReceiveValue(Uri[] value) {
                            if (value != null && value.length > 0) {
                                for (Uri uri : value) {
                                    valueCallback.onReceiveValue(uri);
                                }
                            }
                        }
                    };
                    fileChooserListener.onShowFileChooser(BridgeWebView.this,
                            filePathCallback, new String[]{acceptType});
                }
            }
        };
    }


    /**
     * 创建默认的 WebViewClient
     */
    protected WebViewClient initWebViewClient() {
        return new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================onPageStarted======================");
                    Log.i(TAG, "url=" + url);
                }
                if ((interceptRedirected(url)) || (urlInterceptor != null && urlInterceptor.check(url))) {
                    view.stopLoading();
                    goBack();
                    return;
                }
                webPageInfo.finishTime = 0;
                webPageInfo.webUrl = url;
                webPageInfo.waitInit = true;
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageStart(view, url, favicon);
                    }
                }
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
                webPageInfo.waitInit = false;
                webPageInfo.loadComplete = true;
                webPageInfo.finishTime = System.currentTimeMillis();
                if (webPageListener != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webPageListener.onReceivedTitle(getTitle());
                            webPageListener.onReceivedIcon(getFavicon());
                        }
                    });
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (logLevel >= WebViewLogLevel.LEVEL_ALL) {
                    Log.i(TAG, "========================doUpdateVisitedHistory======================");
                    Log.i(TAG, "url=" + url);
                    Log.i(TAG, "isReload=" + isReload);
                }
                if (isTraceless) {
                    view.clearHistory();
                }
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, WebResourceRequest request) {
                final String url = request.getUrl().toString();
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================shouldOverrideUrlLoading======================");
                    Log.i(TAG, "url=" + url);
                }
                return handleOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================shouldOverrideUrlLoading======================");
                    Log.i(TAG, "url=" + url);
                }
                return handleOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                    Log.i(TAG, "========================onReceivedError======================");
                    if (request != null) {
                        Log.i(TAG, "request=" + request.toString());
                    }
                    if (error != null) {
                        Log.i(TAG, "error=" + error.toString());
                    }
                }
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageError(view, request, error);
                    }
                }
                webPageInfo.waitInit = false;
                webPageInfo.loadComplete = true;
                webPageInfo.finishTime = System.currentTimeMillis();
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (logLevel >= WebViewLogLevel.LEVEL_MOST) {
                    Log.i(TAG, "========================onReceivedSslError======================");
                    if (error != null) {
                        Log.i(TAG, "error=" + error.toString());
                    }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initJsBridge();
                }
            });
        }

        @JavascriptInterface
        public void connect() {
            webPageInfo.waitInit = false;
            if (logLevel >= WebViewLogLevel.LEVEL_NORMAL) {
                Log.i(TAG, "========================jsBridge connect======================");
            }
        }

        //当前线程为JsBridge
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
            if (realParser == null) {
                result = jsCallNativeListener.call(StringParseType.TYPE_UNKNOWN, params, action, callBackName);
            } else {
                StringParseResult parseResult = realParser.parseString(params);
                if (parseResult == null) {
                    result = jsCallNativeListener.call(StringParseType.TYPE_UNKNOWN, params, action, callBackName);
                } else {
                    result = jsCallNativeListener.call(parseResult.getType(), parseResult.getData(), action, callBackName);
                }
            }
            if (result == null) {
                return "";
            } else {
                onCallBack(callBackName, result);
                if (realParser == null) {
                    return result.toString();
                } else {
                    return realParser.toString(result);
                }
            }
        }
    }

    private boolean checkRedirect(String url) {
        //增加一个时间校验
        webPageInfo.isRedirect = (!webPageInfo.loadComplete || (System.currentTimeMillis() - webPageInfo.finishTime) < 50
                || !TextUtils.equals(url, webPageInfo.webUrl));
        return webPageInfo.isRedirect;
    }

    private boolean interceptRedirected(String url) {
        if (TextUtils.equals(url, webPageInfo.webUrl)) {
            return false;
        }
        Boolean flag = redirectedUrlMap.get(url);
        if (flag == null) {
            return false;
        } else {
            return flag;
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
            runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (canGoBack()) {
                    BridgeWebView.super.goBack();
                } else if (getContext() != null || getContext() instanceof Activity) {
                    try {
                        ((Activity) getContext()).finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean handleOverrideUrlLoading(WebView webView, final String url) {
        boolean intercept = urlInterceptor != null && urlInterceptor.check(url);
        if (intercept) {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
        } else if (!url.startsWith("http://") && !url.startsWith("https://")
                && !url.startsWith("file://") && !url.equals("about:blank")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, "activity not found to handle uri scheme for: " + url);
            }
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (overrideLoadUrlListener == null) {
                        if (webPageInfo.webUrl != null && checkRedirect(url)) {
                            redirectedUrlMap.put(webPageInfo.webUrl, true);
                        }
                        loadUrl(url);
                    } else {
                        overrideLoadUrlListener.overrideLoadUrl(url, checkRedirect(url));
                    }
                }
            });
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
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

    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            post(action);
        } else {
            action.run();
        }
    }

    public void setDebug(boolean isDebug, @WebViewLogLevel int level) {
        this.logLevel = level;
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(isDebug);
        }
    }

    public void setJsonParser(JsonParserInterface realParser) {
        this.realParser = realParser;
    }

    public void setUrlInterceptor(IChecker<String> urlInterceptor) {
        this.urlInterceptor = urlInterceptor;
    }

    public void setFileChooserListener(WebViewFileChooserListener fileChooserListener) {
        this.fileChooserListener = fileChooserListener;
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

    public void setProgressListener(OnProgressListener<WebView> progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }

    public void setOverrideLoadUrlListener(OverrideLoadUrlListener overrideLoadUrlListener) {
        this.overrideLoadUrlListener = overrideLoadUrlListener;
    }

    public boolean isTraceless() {
        return isTraceless;
    }

    public void setTraceless(boolean traceless) {
        isTraceless = traceless;
    }

    public boolean isUseWebTitle() {
        return useWebTitle;
    }

    public void setUseWebTitle(boolean useWebTitle) {
        this.useWebTitle = useWebTitle;
    }
}
