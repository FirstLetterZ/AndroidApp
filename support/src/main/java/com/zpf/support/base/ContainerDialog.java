package com.zpf.support.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnAttachListener;
import com.zpf.api.OnDestroyListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewState;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.tool.expand.util.Logger;
import com.zpf.tool.global.CentralManager;
import com.zpf.views.window.ICustomWindow;
import com.zpf.views.window.ICustomWindowManager;

import java.lang.reflect.Type;

/**
 * 基于Dialog的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class ContainerDialog extends Dialog implements ICustomWindow, IViewContainer,
        OnDestroyListener, OnActivityResultListener, OnPermissionResultListener {
    protected final ContainerListenerController mController = new ContainerListenerController();
    private Bundle mParams;
    private IViewProcessor mViewProcessor;
    private final IViewContainer mParentContainer;
    private ICustomWindowManager mWindowManager;

    public ContainerDialog(@NonNull IViewContainer viewContainer, Class<? extends IViewProcessor> targetClass) {
        this(viewContainer, 0, targetClass, null);
    }

    public ContainerDialog(@NonNull IViewContainer viewContainer, Class<? extends IViewProcessor> targetClass,
                           @Nullable Bundle params) {
        this(viewContainer, 0, targetClass, params);
    }

    public ContainerDialog(@NonNull IViewContainer viewContainer, int themeResId,
                           Class<? extends IViewProcessor> targetClass, @Nullable Bundle params) {
        super(viewContainer.getCurrentActivity(), themeResId);
        CentralManager.onObjectInit(this);
        mParentContainer = viewContainer;
        mParams = params;
        if (mViewProcessor == null) {
            mViewProcessor = ContainerController.createViewProcessor(this, targetClass);
            if (mViewProcessor != null) {
                mController.add(mViewProcessor, null);
            } else {
                Logger.w("IViewProcessor is null!");
            }
        }
        Window window = getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);// 取消标题
            window.getDecorView().setPadding(0, 0, 0, 0);
            initWindow(window);
        }
        initView();
    }

    protected void initWindow(@NonNull Window window) {
        boolean useDefWindowConfig = true;
        if (mViewProcessor != null) {
            useDefWindowConfig = !mViewProcessor.initWindow(window);
        }
        if (useDefWindowConfig) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    protected void initView() {
        if (mViewProcessor != null) {
            setContentView(mViewProcessor.getView());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        if (mWindowManager != null) {
            mWindowManager.onShow(this);
        }
        mParentContainer.add(this, OnDestroyListener.class);
        mParentContainer.add(this, OnActivityResultListener.class);
        mParentContainer.add(this, OnPermissionResultListener.class);
        mController.onStart();
        onVisibleChanged(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWindowManager != null) {
            mWindowManager.onClose(this);
        }
        mController.onStop();
        onVisibleChanged(false);
        mParentContainer.remove(this, OnDestroyListener.class);
        mParentContainer.remove(this, OnActivityResultListener.class);
        mParentContainer.remove(this, OnPermissionResultListener.class);
    }

    @Override
    public void onDestroy() {
        mController.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (!mController.onInterceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mController.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mController.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle outState = super.onSaveInstanceState();
        mController.onSaveInstanceState(outState);
        return outState;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mController.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Activity getCurrentActivity() {
        return mParentContainer.getCurrentActivity();
    }

    @Override
    public ICustomWindow setManager(ICustomWindowManager iCustomWindowManager) {
        mWindowManager = iCustomWindowManager;
        return this;
    }

    @Override
    public ICustomWindowManager getCustomWindowManager() {
        return mWindowManager;
    }

    @Override
    public void show() {
        if (mWindowManager == null || mWindowManager.shouldShowImmediately(this)) {
            super.show();
        }
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        mParentContainer.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @NonNull OnActivityResultListener listener) {
        mParentContainer.startActivityForResult(intent, listener);
    }

    @Override
    public IViewState getState() {
        return mController;
    }

    @Override
    public boolean close() {
        return mParentContainer.close();
    }

    @Override
    public IManager<ICancelable> getCancelableManager() {
        return mController.getCancelableManager();
    }

    @Override
    public boolean add(@NonNull Object listener, @Nullable Type listenerClass) {
        return mController.add(listener, listenerClass);
    }

    @Override
    public boolean remove(@NonNull Object listener, @Nullable Type listenerClass) {
        return mController.remove(listener, listenerClass);
    }

    @Override
    public int size(@Nullable Type asType) {
        return mController.size(asType);
    }

    @Override
    public boolean hideLoading() {
        return mParentContainer.hideLoading();
    }

    @Override
    public void setLoadingListener(OnAttachListener onAttachListener) {
        mParentContainer.setLoadingListener(onAttachListener);
    }

    @Override
    public void showLoading() {
        mParentContainer.showLoading();
    }

    @Override
    public void showLoading(Object msg) {
        mParentContainer.showLoading(msg);
    }

    @Override
    public Object invoke(String name, Object params) {
        return null;
    }

    @Override
    public void setLoadingManager(ILoadingManager loadingManager) {
        mParentContainer.setLoadingManager(loadingManager);
    }

    @NonNull
    @Override
    public Bundle getParams() {
        if (mParams == null) {
            mParams = new Bundle();
        }
        return mParams;
    }

    @Override
    public int getContainerType() {
        return ContainerType.CONTAINER_DIALOG;
    }

    @Override
    public boolean setProcessorLinker(IViewLinker linker) {
        try {
            mViewProcessor.onReceiveLinker(linker);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public IViewContainer getParentContainer() {
        return mParentContainer;
    }

    @Override
    @Nullable
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        mController.onParamChanged(newParams);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        mController.onVisibleChanged(visible);
    }

    public void setArguments(@Nullable Bundle args) {
        if (mParams != null) {
            if (args != null) {
                mParams.putAll(args);
            }
        } else {
            mParams = args;
        }
        onParamChanged(mParams);
    }
}