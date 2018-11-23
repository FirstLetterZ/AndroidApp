生成pom命令:
    ./gradlew -p XXX clean install
单独上传：其中 XXX 为 module库名
    ./gradlew -p XXX clean install bintrayUpload --info
各module上传：
    ./gradlew -p api clean install bintrayUpload --info
    ./gradlew -p toolkit clean install bintrayUpload --info
    ./gradlew -p support clean install bintrayUpload --info
    ./gradlew -p glideutil clean install bintrayUpload --info
    ./gradlew -p network clean install bintrayUpload --info
    ./gradlew -p rxnetwork clean install bintrayUpload --info
    ./gradlew -p gsonParse clean install bintrayUpload --info
    ./gradlew -p permission clean install bintrayUpload --info
    ./gradlew -p refresh clean install bintrayUpload --info
    ./gradlew -p webview clean install bintrayUpload --info
    ./gradlew -p dhl clean install bintrayUpload --info
    ./gradlew -p dataparser clean install bintrayUpload --info
全部上传：
    ./gradlew clean install bintrayUpload --info

注意：
第3方依赖统一配置

检查包是否上传成功地址：
https://dl.bintray.com/letterz/{mavenName}

包的引用地址
allprojects {
    repositories {
            maven {
            	url 'https://dl.bintray.com/letterz'
        	}
    }
}
