package com.zpf.support.interfaces;


/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface ViewContainerInterface extends LifecycleListenerController, ActivityController
        , PermissionCheckerInterface {

    RootLayoutInterface getRootLayout();

    TitleBarInterface getTitleBar();

    void showLoading();

    void showLoading(String msg);

    boolean hideLoading();

    SafeWindowInterface getProgressDialog();

    Object invoke(String name, Object params);

}
