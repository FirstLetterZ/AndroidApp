拓展工具
=========
基于api-kit、api-parse与tool-kit封装的方法和视图
### 使用方法
### 依赖
com.zpf.android:api-kit<br>
com.zpf.android:api-parse<br>
com.zpf.android:tool-kit<br>
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
>'com.zpf.android:tool-expand:latest.integration'