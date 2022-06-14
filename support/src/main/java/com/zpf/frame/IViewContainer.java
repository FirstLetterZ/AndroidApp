package com.zpf.frame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IGroup;
import com.zpf.api.IManager;
import com.zpf.api.INavigator;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnViewStateChangedListener;
import com.zpf.tool.stack.LifecycleState;
import com.zpf.views.window.ICustomWindowManager;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface IViewContainer extends ILoadingManager, IGroup, OnViewStateChangedListener {

    Object invoke(String name, Object params);

    void setLoadingManager(ILoadingManager manager);

    int getContainerType();

    boolean setProcessorLinker(IViewLinker linker);

    @Nullable
    IViewContainer getParentContainer();

    @Nullable
    IViewProcessor getViewProcessor();

    void startActivityForResult(@NonNull Intent intent, int requestCode);

    void startActivityForResult(@NonNull Intent intent, @NonNull OnActivityResultListener listener);

    //获取当前状态
    IViewState getState();

    //绑定生命周期的请求控制器
    IManager<ICancelable> getCancelableManager();

    @NonNull
    Bundle getParams();

    Context getContext();

    Activity getCurrentActivity();

    ICustomWindowManager getCustomWindowManager();

    boolean close();
}