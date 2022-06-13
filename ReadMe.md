Android工具库
=========
### 主要内容包括：

项目内的各项依赖库版本由[build](./build.gradle)文件统一管理

工具库发布
---------
#### 单项目发布
> ./gradlew -p XXX clean build publish --info
 
其中 XXX 为 module库名，详细如下表：

>./gradlew -p toolexpand clean build publish --info
./gradlew -p glideutil clean build publish --info
./gradlew -p gsonParse clean build publish --info
./gradlew -p refresh clean build publish --info
./gradlew -p webview clean build publish --info
./gradlew -p support clean build publish --info
./gradlew -p network clean build publish --info
./gradlew -p rvexpand clean build publish --info

#### 全部发布
>./gradlew clean install bintrayUpload --info

#### 通过以下地址检查工具包是否上传成功
>https://dl.bintray.com/letterz/{mavenName}

引用
---------
在项目对应build.gradle文件内的allprojects-repositories下添加工具包仓库地址：
````
allprojects {
    repositories {
            maven { url 'http://repo.shenmajr.com/content/repositories/releases' }
    }
}
````
在Module内添加对应引用；
