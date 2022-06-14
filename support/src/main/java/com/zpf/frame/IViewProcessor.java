package com.zpf.frame;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICancelable;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.IManager;
import com.zpf.api.INavigator;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.api.OnTouchKeyListener;
import com.zpf.api.OnViewStateChangedListener;
import com.zpf.tool.stack.LifecycleState;
import com.zpf.views.window.ICustomWindowManager;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface IViewProcessor extends IListenerSet {
    void runWithPermission(Runnable runnable, String... permissions);

    void runAfterVisible(Runnable runnable, boolean nextTime);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

    <T extends View> T find(@IdRes int viewId);

    View getView();

    Context getContext();

    boolean initWindow(@NonNull Window window);

    void onReceiveLinker(IViewLinker linker);

    Activity getCurrentActivity();

    //获取当前状态
    IViewState getState();

    //绑定生命周期的请求控制器
    IManager<ICancelable> getCancelableManager();

    @NonNull
    Bundle getParams();

    ICustomWindowManager getCustomWindowManager();

    boolean close();
}
