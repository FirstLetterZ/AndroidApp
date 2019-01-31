RxAndroid拓展网络工具
=========
* [RxCall](src/main/java/com/zpf/support/rxnetwork/RxCall.java)
---复写SmBaseCall，改成RxAndroid订阅模式；
* [RxCallBack](src/main/java/com/zpf/support/rxnetwork/RxCallBack.java)
---复写SmBaseCallBack，改成RxAndroid订阅模式；
* [RxResultCallBack](src/main/java/com/zpf/support/rxnetwork/RxResultCallBack.java)
---RxCallBack的返回HttpResult格式的网络响应数据处理；

### 使用方法
同shenma_android_network，其余特性见RxAndroid；
### 依赖
com.squareup.retrofit2:adapter-rxjava2<br>
io.reactivex.rxjava2:rxandroid<br>
io.reactivex.rxjava2:rxjava<br>
com.shenmajr-android:shenma_android_network<br>
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
>'com.zpf.android:util-rxNetwork:latest.integration'
