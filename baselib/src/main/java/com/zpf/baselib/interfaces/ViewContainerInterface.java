package com.zpf.baselib.interfaces;

import android.content.Context;
import android.content.Intent;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface ViewContainerInterface extends LifecycleListenerController {

    //获取上下文
    Context getContext();

    Intent getIntent();

    RootLayoutInterface getRootLayout();

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);

    void finishWithResult(int resultCode, Intent data);

    void finish();

    void showLoading();

    void showLoading(String msg);

    boolean hideLoading();

}
