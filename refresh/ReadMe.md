下拉刷新上拉加载
=========
* [BaseViewStateCheckImpl](./src/main/java/com/zpf/refresh/util/BaseViewStateCheckImpl.java)
---默认实现的视图状态检查，视图手动布局工具；
* [HeadFootImpl](./src/main/java/com/zpf/refresh/util/HeadFootImpl.java)
---默认实现的头或脚布局；
* [HeadFootInterface](./src/main/java/com/zpf/refresh/util/HeadFootInterface.java)
---头或脚布局接口；
* [OnRefreshListener](./src/main/java/com/zpf/refresh/util/OnRefreshListener.java)
---刷新、加载监听；
* [RefreshLayoutType](./src/main/java/com/zpf/refresh/util/RefreshLayoutType.java)
---模式枚举；
* [ViewBorderUtil](./src/main/java/com/zpf/refresh/util/ViewBorderUtil.java)
---视图滑动到顶部或底部的判断；
* [ViewStateCheckListener](./src/main/java/com/zpf/refresh/ViewStateCheckListener.java)
---视图状态检查，视图手动布局接口；
* [HeadFootLayout](./src/main/java/com/zpf/refresh/view/HeadFootLayout.java)
---头或脚布局容器；
* [RefreshLayout](./src/main/java/com/zpf/refresh/view/RefreshLayout.java)
---下拉刷新上拉加载布局容器；
* [StickyNavLayout](./src/main/java/com/zpf/refresh/view/StickyNavLayout.java)
---悬浮第二个子布局的布局容器；

### 使用方法
1. 根据需要替换默认的头、脚布局实现HeadFootImpl，默认的状态判断及自视图布局实现BaseViewStateCheckImpl；
2. 使用RefreshLayout在XML布局中包裹内容布局;
3. 设置布局模式(setType)，设置监听回调(setOnRefreshListener)；
````
<RefreshLayout>
    <ScrollView>
        <View>
        </View>
    </ScrollView>
</RefreshLayout>
````
### 依赖
com.zpf.android:api-kit<br>
com.android.support:support-annotations<br>
com.android.support:recyclerview-v7<br>
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
>'com.zpf.android:pullRefresh:latest.integration'
 