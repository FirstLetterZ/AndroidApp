package com.zpf.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zpf.api.IChecker;
import com.zpf.api.ILogger;
import com.zpf.api.OnProgressListener;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.api.dataparser.StringParseResult;
import com.zpf.api.dataparser.StringParseType;
import com.zpf.tool.global.CentralManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZPF on 2018/7/25.
 */
public class BridgeWebView extends WebView {
    private List<String> whiteList;//白名单关键字
    private String jsBridgeString;
    private final List<WebViewStateListener> stateListenerList = new ArrayList<>();
    protected OnReceivedWebPageListener webPageListener;
    protected WebViewWindowListener windowListener;
    protected WebViewFileChooserListener fileChooserListener;
    protected OnProgressListener progressChangedListener;
    protected IChecker<String> urlInterceptor;//url拦截
    protected JsCallNativeListener jsCallNativeListener;//js调用native的回调
    protected OverrideLoadUrlHandler overrideLoadUrlHandler;//OverrideLoadUrlListener
    private boolean isTraceless;//无痕浏览
    private final WebPageInfo webPageInfo = new WebPageInfo();//当前加载页面
    private final HashMap<String, Boolean> redirectedUrlMap = new HashMap<>();//重定向原始url集合
    private JsonParserInterface realParser = CentralManager.getInstance(JsonParserInterface.class);//json解析
    private View customView = null;
    private WebChromeClient.CustomViewCallback customCallback = null;
    private WebViewScrollListener scrollChangeListener = null;
    private String TAG;
    private ILogger realLogger = CentralManager.getInstance(ILogger.class);

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
        jsBridgeString = initJsBridgeString();
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });
        CentralManager.onObjectInit(this);
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

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollChangeListener != null) {
            scrollChangeListener.onScrollChange(l, t, oldl, oldt);
        }
    }

    /**
     * 创建默认的 WebChromeClient
     */
    protected WebChromeClient initWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (webPageInfo.waitBridgeInit) {
                    webPageInfo.waitBridgeInit = injectBridgeScript();
                }
                logInfo("========================onReceivedTitle======================");
                logInfo("url=" + view.getUrl());
                logInfo("title=" + title);
                if (webPageListener != null) {
                    webPageListener.onReceivedTitle(title);
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                if (webPageInfo.waitBridgeInit) {
                    webPageInfo.waitBridgeInit = injectBridgeScript();
                }
                logInfo("========================onReceivedIcon======================");
                logInfo("url=" + view.getUrl());
                if (icon != null) {
                    logInfo("icon size:width=" + icon.getWidth() + " ;height=" + icon.getHeight());
                } else {
                    logInfo("icon == null");
                }
                if (webPageListener != null) {
                    webPageListener.onReceivedIcon(icon);
                }
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                logInfo("========================onProgressChanged======================");
                logInfo("url=" + view.getUrl());
                logInfo("newProgress=" + newProgress);
                if (webPageInfo.waitBridgeInit && newProgress > 10) {
                    injectBridgeScript();
                }
                if (progressChangedListener != null) {
                    progressChangedListener.onProgress(100, newProgress, view);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                logInfo("========================onCreateWindow======================");
                logInfo("url=" + view.getUrl());
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
                logInfo("========================onCloseWindow======================");
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

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                logInfo("sourceId=" + consoleMessage.sourceId());
                logInfo("message=" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
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
                logInfo("========================onPageStarted======================");
                logInfo("url=" + url);
                if ((interceptRedirected(url)) || (urlInterceptor != null && urlInterceptor.check(url))) {
                    view.stopLoading();
                    goBack();
                    return;
                }
                webPageInfo.finishTime = 0;
                webPageInfo.webUrl = url;
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageStart(view, url, favicon);
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                logInfo("========================onPageFinished======================");
                logInfo("url=" + url);
                if (urlInterceptor != null && urlInterceptor.check(url)) {
                    goBack();
                    return;
                }
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageFinish(view, url);
                    }
                }
                webPageInfo.waitBridgeInit = false;
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
                logInfo("========================doUpdateVisitedHistory======================");
                logInfo("url=" + url);
                logInfo("isReload=" + isReload);
                if (isTraceless) {
                    view.clearHistory();
                }
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, WebResourceRequest request) {
                final String url = request.getUrl().toString();
                logInfo("========================shouldOverrideUrlLoading======================");
                logInfo("url=" + url);
                return handleOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                logInfo("========================shouldOverrideUrlLoading======================");
                logInfo("url=" + url);
                return handleOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                logInfo("========================onReceivedError======================");
                logInfo("request.url=" + failingUrl);
                logInfo("error.code=" + errorCode);
                logInfo("error.description=" + description);
                if (stateListenerList.size() > 0) {
                    for (WebViewStateListener listener : stateListenerList) {
                        listener.onPageError(view, failingUrl, errorCode, description);
                    }
                }
                webPageInfo.waitBridgeInit = false;
                webPageInfo.loadComplete = true;
                webPageInfo.finishTime = System.currentTimeMillis();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                int code = 0;
                String description = null;
                String url = null;
                boolean isForMainFrame = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isForMainFrame = request.isForMainFrame();
                    url = request.getUrl().toString();
                    code = error.getErrorCode();
                    description = error.getDescription().toString();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isForMainFrame = request.isForMainFrame();
                    url = request.getUrl().toString();
                }
                if (isForMainFrame) {
                    onReceivedError(BridgeWebView.this, code, description, url);
                } else {
                    logInfo("========================onReceivedFrameError======================");
                    logInfo("request.url=" + url);
                    logInfo("error.code=" + code);
                    logInfo("error.description=" + description);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                logInfo("========================onReceivedSslError======================");
                if (error != null) {
                    logInfo("error=" + error);
                }
                handler.proceed(); // 接受证书
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                logInfo("========================shouldInterceptRequest======================");
                logInfo("url=" + url);
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
        public void connect() {
            webPageInfo.waitBridgeInit = false;
            logInfo("========================jsBridge connect======================");
        }

        //当前线程为JsBridge
        @JavascriptInterface
        public String callNative(String action, String params, String callBackName) {
            logInfo("========================JavaScriptInterface callNative======================");
            logInfo("action=" + action);
            logInfo("params=" + params);
            logInfo("callBackName=" + callBackName);
            if (jsCallNativeListener == null || TextUtils.isEmpty(action)) {
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
        String currentUrl = webPageInfo.webUrl;
        if (currentUrl == null) {
            return false;
        }
        boolean isRedirect = !TextUtils.equals(currentUrl, url) &&
                (!webPageInfo.loadComplete || (System.currentTimeMillis() - webPageInfo.finishTime) < 50);
        webPageInfo.isRedirect = isRedirect;
        redirectedUrlMap.put(currentUrl, isRedirect);
        return isRedirect;
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
        if (fucName == null || fucName.length() == 0) {
            return;
        }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logInfo("======================== onCallBack ======================");
                logInfo("javascript=" + javascript);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    evaluateJavascript(javascript, null);
                } else {
                    loadUrl(javascript);
                }
            }
        });
    }

    @Override
    public void goBack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (canGoBack()) {
                    BridgeWebView.super.goBack();
                } else {
                    Context context = getContext();
                    if (!(context instanceof Activity) && context instanceof ContextWrapper) {
                        context = ((ContextWrapper) context).getBaseContext();
                    }
                    if (context instanceof Activity) {
                        try {
                            ((Activity) context).finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void clearAllCache() {
        clearCache(true);
        clearFormData();
        clearHistory();
        WebStorage.getInstance().deleteAllData();
    }

    private void logInfo(String message) {
        if (CentralManager.debuggable()) {
            String realTag = TAG;
            if (realTag == null) {
                realTag = getClass().getSimpleName();
            }
            if (realLogger != null) {
                realLogger.log(Log.INFO, realTag, message);
            } else {
                Log.i(realTag, message);
            }
        }
    }

    protected boolean handleOverrideUrlLoading(WebView webView, final String url) {
        boolean intercept = urlInterceptor != null && urlInterceptor.check(url);
        if (intercept) {
            logInfo("intercept url: " + url);
            return true;
        }
        boolean openBySystem = !url.startsWith("http://") && !url.startsWith("https://")
                && !url.startsWith("file://") && !url.equals("about:blank");
        if (openBySystem) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                logInfo("activity not found to handle uri scheme for: " + url);
            }
            return true;
        }
        final boolean isRedirect = checkRedirect(url);
        final OverrideLoadUrlHandler handler = overrideLoadUrlHandler;
        if (handler != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    overrideLoadUrlHandler.overrideLoadUrl(url, isRedirect);
                }
            });
            return true;
        }
        return false;
    }

    protected String initJsBridgeString() {
        return DefaultJsReader.loadJsFromAssetsFile(getContext(), true);
    }

    protected boolean injectBridgeScript() {
        if (jsBridgeString != null) {
            logInfo("start connect JsBridge");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                evaluateJavascript("javascript:" + jsBridgeString, null);
            } else {
                loadUrl("javascript:" + jsBridgeString);
            }
            return true;
        } else {
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

    public void setLogger(String tag, ILogger logger) {
        realLogger = logger;
        TAG = tag;
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

    public void setScrollChangeListener(WebViewScrollListener scrollChangeListener) {
        this.scrollChangeListener = scrollChangeListener;
    }

    public void setWebPageListener(OnReceivedWebPageListener webPageListener) {
        this.webPageListener = webPageListener;
    }

    public void setWindowListener(WebViewWindowListener windowListener) {
        this.windowListener = windowListener;
    }

    public void setProgressListener(OnProgressListener progressChangedListener) {
        this.progressChangedListener = progressChangedListener;
    }

    public void setOverrideLoadUrlListener(OverrideLoadUrlHandler overrideLoadUrlHandler) {
        this.overrideLoadUrlHandler = overrideLoadUrlHandler;
    }

    public boolean isTraceless() {
        return isTraceless;
    }

    public void setTraceless(boolean traceless) {
        isTraceless = traceless;
    }

}
