BridgeWebView
=========
基于原生WebView封装，可添加WebView状态监听(WebViewStateListener)，可添加WebView进度监听(OnProgressChangedListener)，<br>
可添加WebViewUrl加载拦截(UrlInterceptor)，可添加WebView标题和图标获取监听(OnReceivedWebPageListener)；<br>
配置了默认的jsBridge实现，可以通过在asset文件夹下创建bridge.js文件替换默认的jsBridge实现；<br>
通过JsCallNativeListener获取js调用原生请求，由于Android只能接受String类型数据，所以使用gson对数据解析，<br>
回调参数分别为：type--数据解析类型；object--解析后的数据；action--方法名称；callBackName--回调方法名称；
### 使用方法
1. 通过addView的方式将视图添加到页面；
2. 设置json解析器(setJsonParser)，设置JsCallNativeListener，根据需求设置其他监听；
``````
public void onDestroy() {
    bridgeWebView.setJsCallNativeListener(null);
    removeView(bridgeWebView);
    bridgeWebView.destroy();
    ...
}
``````
### 依赖
com.zpf.android:api-base<br>
com.zpf.android:api-parse
### 引用
在项目对应build.gradle文件内的allprojects-repositories下添加工具包仓库地址：
``````
allprojects {
    repositories {
        maven { url 'https://dl.bintray.com/letterz/AndroidSupportMaven' }
    }
}
``````
在Module内添加对应引用：
>'com.zpf.android:bridgeWebview:latest.integration' 