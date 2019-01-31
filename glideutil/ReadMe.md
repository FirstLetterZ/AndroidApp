glide工具
=========
配置GlideModule，配置Gilde使用Okhttp网络库，配置自定义GlideUrl<br>
### 使用方法
在AnroidManifest中配置GlideModule<br>
````
<meta-data
    android:name="com.zpf.tool.glideutil.OkHttpGlideModule"
    android:value="GlideModule" />
````
其他使用方法与Glide一致
### 依赖
com.squareup.okhttp3:okhttp<br>
com.github.bumptech.glide:glide<br>
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
>'com.zpf.android:util-glide:latest.integration'