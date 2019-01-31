网络处理
=========
使用okhttp处理网络请求，使用retrofit处理请求接口及返回数据，使用gson解析数据<br>
1. base文件夹
* [ErrorCode](src/main/java/com/zpf/support/network/base/ErrorCode.java)
---定义了部分常见网络异常对应code；
* [BaseCall](src/main/java/com/zpf/support/network/base/BaseCall.java)
---网络请求接口处理；
* [BaseCallBack](src/main/java/com/zpf/support/network/base/BaseCallBack.java)
---网络响应数据处理的基类；
2. header文件夹
* [ConstantHeader](src/main/java/com/zpf/support/network/header/ConstantHeader.java)
---对应值为常量的头信息条目；
* [HeaderCarrier](src/main/java/com/zpf/support/network/header/HeaderCarrier.java)
---完整的请求头信息；
* [VolatileHeader](src/main/java/com/zpf/support/network/header/VolatileHeader.java)
---对应值为变量的头信息条目；
3. interceptor文件夹
* [DownLoadInterceptor](src/main/java/com/zpf/support/network/interceptor/DownLoadInterceptor.java)
---用于监听下载进度；
* [HeaderInterceptor](src/main/java/com/zpf/support/network/interceptor/HeaderInterceptor.java)
---用于添加头信息；
* [NetLogInterceptor](src/main/java/com/zpf/support/network/interceptor/NetLogInterceptor.java)
---用于收集网络请求及返回信息；
4. model文件夹
* [ClientBuilder](src/main/java/com/zpf/support/network/model/ClientBuilder.java)
---创建网络请求客户端；
* [CustomException](src/main/java/com/zpf/support/network/model/CustomException.java)
---自定义网络报错；
* [HttpResult](src/main/java/com/zpf/support/network/model/HttpResult.java)
---code、data、message格式的网络响应数据；
* [ProgressResponseBody](src/main/java/com/zpf/support/network/model/ProgressResponseBody.java)
---网络响应进度信息；
* [TLSSocketFactory](src/main/java/com/zpf/support/network/model/TLSSocketFactory.java)
---服务器加密协议及签名认证；
5. retrofit文件夹
* [ResponseCallBack](src/main/java/com/zpf/support/network/retrofit/ResponseCallBack.java)
---返回HttpResult格式的网络响应数据处理；
* [ResultCallBack](src/main/java/com/zpf/support/network/retrofit/ResultCallBack.java)
---网络响应数据处理窗；

### 使用方法
1. 继承BaseCall，在初始化时配请求头信息，完成网络接口的调用，如有需要则复写builder()方法，配置Client拦截器；
````
HeaderCarrier headerCarrier = new HeaderCarrier();
headerCarrier.addHeader(new ConstantHeader("key","value"))
        .addHeader(new VolatileHeader("key", new VariableParameterInterface() {
            @Override
            public Object getCurrentValue() {
                return "value for the key";
            }
        }));
````
2. 根据网络响应选择合适的CallBack及ResponseBody，在CallBack中处理返回数据；
3. 其余方法见okhttp及retrofit；
### 依赖
com.squareup.okhttp3:okhttp<br>
com.squareup.retrofit2:retrofit<br>
com.squareup.retrofit2:converter-gson<br>
com.zpf.android:api-kit<br>
com.zpf.android:tool-kit<br>
com.zpf.android:util-gson<br>
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
>'com.zpf.android:util-retrofit:latest.integration'