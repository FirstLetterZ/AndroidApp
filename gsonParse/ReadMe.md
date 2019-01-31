gson解析工具
=========
### 使用方法
GsonUtil.get()获取单例
### 依赖
com.google.code.gson:gson<br>
com.zpf:api-parse
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
>'com.zpf.android:util-gson:latest.integration'