package com.zpf.support.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnDestroyListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.INavigator;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.config.LifecycleState;
import com.zpf.tool.config.MainHandler;
import com.zpf.tool.config.stack.IStackItem;
import com.zpf.tool.expand.view.CustomDialog;

/**
 * 基于Activity的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class ContainerDialog extends CustomDialog implements IViewContainer, OnDestroyListener, OnActivityResultListener {
    protected final ContainerListenerController mController = new ContainerListenerController();
    private Bundle mParams;
    private IViewProcessor mViewProcessor;
    private IViewContainer mParentContainer;

    public ContainerDialog(@NonNull IViewContainer viewContainer, Class<? extends IViewProcessor> targetClass) {
        this(viewContainer, 0, targetClass);
    }

    public ContainerDialog(@NonNull IViewContainer viewContainer, int themeResId, Class<? extends IViewProcessor> targetClass) {
        super(viewContainer.getContext(), themeResId);
        mParentContainer.addListener(this);
        mParentContainer = viewContainer;
        if (mViewProcessor == null) {
            mViewProcessor =  ContainerController.createViewProcessor(this, targetClass);
            if (mViewProcessor != null) {
                mController.addListener(mViewProcessor);
                setContentView(mViewProcessor.getView());
            } else {
                LogUtil.w("IViewProcessor is null!");
            }
        }
        Window window = getWindow();
        if (window != null && mViewProcessor != null) {
            mViewProcessor.initWindow(window);
        }
    }

    @Override
    protected void initWindow(@NonNull Window window) {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mController.onStart();
        mController.onVisibleChanged(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mController.onStop();
        mController.onVisibleChanged(false);
    }

    @Override
    public void onDestroy() {
        mController.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mController.onInterceptBackPress()) {
            super.onBackPressed();
        }
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

    @NonNull
    @Override
    public IStackItem getStackItem() {
        return mParentContainer.getStackItem();
    }

    @Override
    @LifecycleState
    public int getState() {
        return mController.getState();
    }

    @Override
    public boolean isLiving() {
        return mController.isLiving();
    }

    @Override
    public boolean isActive() {
        return mController.isActive();
    }

    @Override
    public Intent getIntent() {
        return mParentContainer.getIntent();
    }

    @Override
    public Activity getCurrentActivity() {
        return mParentContainer.getCurrentActivity();
    }

    @Override
    public void startActivity(Intent intent) {
        mParentContainer.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        mParentContainer.startActivity(intent, options);
    }

    @Override
    public void startActivities(Intent[] intents) {
        mParentContainer.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, @Nullable Bundle options) {
        mParentContainer.startActivities(intents, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mParentContainer.startActivityForResult(intent, requestCode);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        mParentContainer.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void show(final ICustomWindow window) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mController.show(window);
        } else {
            MainHandler.get().post(new Runnable() {
                @Override
                public void run() {
                    mController.show(window);
                }
            });
        }
    }

    @Override
    public boolean close() {
        return mParentContainer.close();
    }

    @Override
    public IManager<ICancelable> getCancelableManager() {
        return null;
    }

    @Override
    public boolean addListener(Object listener) {
        return mController.addListener(listener);
    }

    @Override
    public boolean removeListener(Object listener) {
        return mController.removeListener(listener);
    }

    @Override
    public void finishWithResult(int resultCode, Intent data) {
        super.dismiss();
        if (mParentContainer instanceof OnActivityResultListener) {
            ((OnActivityResultListener) mParentContainer).onActivityResult(AppConst.DEF_REQUEST_CODE, resultCode, data);
        }
    }

    @Override
    public void finish() {
        super.dismiss();
    }

    @Override
    public boolean hideLoading() {
        return mParentContainer.hideLoading();
    }

    @Override
    public View getLoadingView() {
        return mParentContainer.getLoadingView();
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
    public boolean checkPermissions(String... permissions) {
        return mParentContainer.checkPermissions(permissions);
    }

    @Override
    public boolean checkPermissions(int requestCode, String... permissions) {
        return mParentContainer.checkPermissions(requestCode, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, String... permissions) {
        mParentContainer.checkPermissions(onPermission, onLock, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, int requestCode, String... permissions) {
        mParentContainer.checkPermissions(onPermission, onLock, requestCode, permissions);
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
            mParams = getIntent().getExtras();
            if (mParams == null) {
                mParams = new Bundle();
            }
        }
        return mParams;
    }

    @Override
    public int getContainerType() {
        return ContainerType.CONTAINER_DIALOG;
    }

    @Override
    public IViewContainer getParentContainer() {
        return mParentContainer;
    }

    @Override
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
    }

    @Override
    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
        return null;
    }

}