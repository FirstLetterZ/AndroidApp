框架依赖
=========
1. base文件夹,视图容器与视图处理；
* [ActivityContainer](./src/main/java/com/zpf/support/base/ActivityContainer.java)
---Activity视图容器；
* [CompatActivityContainer](./src/main/java/com/zpf/support/base/CompatActivityContainer.java)
---AppCompatActivity视图容器；
* [CompatFragmentContainer](./src/main/java/com/zpf/support/base/CompatFragmentContainer.java)
---android.support.v4.app.Fragment的视图容器；
* [FragmentContainer](./src/main/java/com/zpf/support/base/FragmentContainer.java)
---android.app.Fragment的视图容器；
* [ContainerProcessor](./src/main/java/com/zpf/support/base/ContainerProcessor.java)
---视图层处理；
2. constant文件夹，全局常量；
* [AppConst](./src/main/java/com/zpf/support/constant/AppConst.java)
---全局常量；
3. defview文件夹，默认实现的容器中使用到的布局；
* [ProgressDialog](./src/main/java/com/zpf/support/defview/ProgressDialog.java)
---默认实现的加载等待弹窗；
* [ProxyCompatContainer](./src/main/java/com/zpf/support/defview/ProxyCompatContainer.java)
---将FragmentActivity或android.support.v4.app.Fragment转为视图容器；
* [ProxyContainer](./src/main/java/com/zpf/support/defview/ProxyContainer.java)
---将Activity或android.app.Fragment转为视图容器；
* [RootLayout](./src/main/java/com/zpf/support/defview/RootLayout.java)
---默认实现的容器层根布局，包含StatusBar、TitleBar及FrameLayout；
* [StatusBar](./src/main/java/com/zpf/support/defview/StatusBar.java)
---默认实现的状态栏；
* [TitleBar](./src/main/java/com/zpf/support/defview/TitleBar.java)
---默认实现的标题栏；
4. util文件夹，工具文件夹；
* [ContainerListenerController](./src/main/java/com/zpf/support/util/ContainerListenerController.java)
---视图容器内生命周期监听、弹窗管理、回调监听、权限申请工具等；
* [ContainerProxyManager](./src/main/java/com/zpf/support/util/ContainerProxyManager.java)
---转换为视图容器的管理工具；
* [LifecycleLogUtil](./src/main/java/com/zpf/support/util/LifecycleLogUtil.java)
---生命周期日志打印；
* [LogUtil](./src/main/java/com/zpf/support/util/LogUtil.java)
---日志打印工具；
* [PermissionUtil](./src/main/java/com/zpf/support/util/PermissionUtil.java)
---权限申请及对应弹窗提示；
* [PhotoUtil](./src/main/java/com/zpf/support/util/PhotoUtil.java)
---拍照或从相册选取；
5. view文件夹，其他常用视图；
* [banner文件夹](./src/main/java/com/zpf/support/view/banner)
---带有指示点的Banner；
* [BottomDialog](./src/main/java/com/zpf/support/view/BottomDialog.java)
---仿iOS从底部弹出的弹窗；
* [CommonDialog](./src/main/java/com/zpf/support/view/CommonDialog.java)
---仿iOS白色弹窗；
* [SelectPhotoDialog](./src/main/java/com/zpf/support/view/SelectPhotoDialog.java)
---从底部弹出的弹窗，选择拍照或者相册选取；
### 使用方法
1. 将Activity继承CompatActivityContainer(或ActivityContainer)；
``````
public class MainActivity extends CompatActivityContainer<MainContainerProcessor>
``````
2. 创建对应的视图层文件(上述代码中的MainContainerProcessor)；
``````
public class MainContainerProcessor extends ContainerProcessor
``````
### 依赖
com.android.support:design<br>
com.zpf.android:com.zpf.android:tool-expand<br>
com.zpf.android:com.zpf.android:tool-permission<br>
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
>'com.zpf.android:tool-support:latest.integration'