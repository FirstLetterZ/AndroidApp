package com.zpf.appLib.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
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
import com.zpf.appLib.util.JsonUtil;
import com.zpf.appLib.util.LogUtil;
import com.zpf.appLib.view.interfaces.CheckUrlListener;
import com.zpf.appLib.view.interfaces.JsCallNativeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ZPF on 2018/4/17.
 */

public class BridgeWebView extends WebView {
    private CheckUrlListener urlCheckListener;
    private JsCallNativeListener jsCallNativeListener;
    private List<String> whiteList;//白名单关键字
    private boolean showLog = true;
    private boolean hasInitBridge;//

    public BridgeWebView(Context context) {
        this(context, null, 0);
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
        otherSetting();
    }


    private void initSetting() {
        WebSettings setting = getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setBuiltInZoomControls(true);
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
    }

    private void otherSetting() {
        addJavascriptInterface(new JavaScriptInterface(), "bridge");
        if (Build.VERSION.SDK_INT >= 19) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        requestFocus();
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (showLog) {
                    LogUtil.i("========================onPageStarted======================");
                    LogUtil.i("url=" + url);
                }
                if (urlCheckListener != null && urlCheckListener.check(url)) {
                    return;
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (showLog) {
                    LogUtil.i("========================onPageFinished======================");
                    LogUtil.i("url=" + url);
                }
                if (urlCheckListener != null && urlCheckListener.check(url)) {
                    return;
                }
                initJsBridge();
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (showLog) {
                    LogUtil.i("========================shouldOverrideUrlLoading======================");
                    LogUtil.i("url=" + url);
                }
                if (urlCheckListener != null) {
                    return urlCheckListener.check(url);
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (showLog) {
                    LogUtil.i("========================onReceivedError======================");
                    LogUtil.i("request=" + JsonUtil.toString(request));
                    LogUtil.i("error=" + JsonUtil.toString(error));
                }
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (showLog) {
                    LogUtil.i("========================onReceivedSslError======================");
                    LogUtil.i("error=" + JsonUtil.toString(error));
                }
                handler.proceed(); // 接受证书

            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (showLog) {
                    LogUtil.i("========================shouldInterceptRequest======================");
                    LogUtil.i("url=" + url);
                }
                boolean pass = false;
                if (whiteList != null && whiteList.size() > 0) {
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
        });
    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void init() {
            initJsBridge();
        }

        @JavascriptInterface
        public String callNative(String action, String params, String callBackName) {
            if (showLog) {
                LogUtil.i("========================JavaScriptInterface callNative======================");
                LogUtil.i("action=" + action);
                LogUtil.i("params=" + params);
                LogUtil.i("callBackName=" + callBackName);
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
                result = jsCallNativeListener.call(JsCallNativeListener.TYPE_NULL, null, action, callBackName);
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
        String forebody = "javascript:NativeAndroidJSBridge('" + fucName + "'";
        if (fucName != null && fucName.length() > 0) {
            if (result == null || "".equals(result)) {
                result = "";
            } else {
                if (result instanceof String) {
                    result = "'" + result + "'";
                }
                forebody = forebody + ",";
            }
            final String javascript = forebody + result + ")";
            post(new Runnable() {
                @Override
                public void run() {
                    loadUrl(javascript);
                }
            });
        }
    }

    public void goBack() {
        if (canGoBack()) {
            goBack();
        } else {
            try {
                ((Activity) getContext()).finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setUrlCheckListener(CheckUrlListener urlCheckListener) {
        this.urlCheckListener = urlCheckListener;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void setJsCallNativeListener(JsCallNativeListener listener) {
        this.jsCallNativeListener = listener;
    }

    public void setDebug(boolean showLog) {
        this.showLog = showLog;
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(showLog);
        }
    }

    //初始化完成后H5可调用asyncJSCall(name, body, callBack)，否需要通过window.bridge调用
    //所有参数皆为String类型
    public void initJsBridge() {
        if (hasInitBridge) {
            return;
        }
        hasInitBridge = true;
        loadUrl("javascript:" +
                "window.NativeAndroid ={" +
                "     actions: []," +
                "};" +
                "window.NativeAndroid.asyncJSCall = function (name, body, callBack) {" +
                "    if (!name) { return; }" +
                "    var date = new Date();" +
                "    var id = date.toISOString();" +
                "    var newAction = {" +
                "        id: id," +
                "        callBack: callBack," +
                "    };" +
                "    NativeAndroid.actions.push(newAction);" +
                "    window.bridge.callNative(name, body, id);" +
                "};" +
                "window.NativeAndroidJSBridge = function(id, body) {" +
                "    if (!id) {" +
                "        return;" +
                "    }" +
                "    if (!NativeAndroid.actions) {" +
                "        return;" +
                "    }" +
                "    for (var index in NativeAndroid.actions) {" +
                "        var action = NativeAndroid.actions[index];" +
                "        if (action.id === id) {" +
                "            var callBack = action.callBack;" +
                "            if (callBack && typeof callBack === 'function') {" +
                "                if (body) {" +
                "                    callBack(body)" +
                "                } else {" +
                "                    callBack()" +
                "                }" +
                "            }" +
                "            break;" +
                "        }" +
                "    }" +
                "};");
    }
}
