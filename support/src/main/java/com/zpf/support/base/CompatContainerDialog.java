package com.zpf.support.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import android.view.Window;
import android.view.WindowManager;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.IPermissionResult;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnDestroyListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.ILoadingStateListener;
import com.zpf.frame.INavigator;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewStateListener;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.config.MainHandler;
import com.zpf.tool.stack.IStackItem;
import com.zpf.tool.stack.LifecycleState;

import java.lang.reflect.Type;

/**
 * 基于AppCompatDialog的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class CompatContainerDialog extends AppCompatDialog implements ICustomWindow, IViewContainer, IViewStateListener,
        OnDestroyListener, OnActivityResultListener {
    protected final ContainerListenerController mController = new ContainerListenerController();
    private Bundle mParams;
    private IViewProcessor mViewProcessor;
    private IViewContainer mParentContainer;
    protected IManager<ICustomWindow> listener;
    protected long bindId = -1;

    public CompatContainerDialog(@NonNull IViewContainer viewContainer, Class<? extends IViewProcessor> targetClass) {
        this(viewContainer, 0, targetClass, null);
    }

    public CompatContainerDialog(@NonNull IViewContainer viewContainer, Class<? extends IViewProcessor> targetClass,
                                 @Nullable Bundle params) {
        this(viewContainer, 0, targetClass, params);
    }

    public CompatContainerDialog(@NonNull IViewContainer viewContainer, int themeResId,
                                 Class<? extends IViewProcessor> targetClass, @Nullable Bundle params) {
        super(viewContainer.getCurrentActivity(), themeResId);
        mParentContainer = viewContainer;
        mParams = params;
        mParentContainer.addListener(this, IBackPressInterceptor.class);
        if (mViewProcessor == null) {
            mViewProcessor = ContainerController.createViewProcessor(this, targetClass);
            if (mViewProcessor != null) {
                mController.addListener(mViewProcessor, null);
            } else {
                LogUtil.w("IViewProcessor is null!");
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
    public void show() {
        show(this);
    }

    @Override
    public CompatContainerDialog toBind(IManager<ICustomWindow> manager) {
        this.listener = manager;
        if (manager != null) {
            bindId = manager.bind(this);
        } else {
            bindId = -1;
        }
        return this;
    }

    @Override
    public boolean unBind(long bindId) {
        if (listener != null) {
            listener.remove(bindId);
            return true;
        }
        return false;
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
        onVisibleChanged(true);
        onActivityChanged(true);
    }

    @Override
    public void onStop() {
        if (listener != null) {
            listener.remove(bindId);
        }
        super.onStop();
        mController.onStop();
        onActivityChanged(false);
        onVisibleChanged(false);
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

    @Override
    public void cancel() {
        mParentContainer.close();
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
        MainHandler.runOnMainTread(new Runnable() {
            @Override
            public void run() {
                if (mParentContainer != null) {
                    mParentContainer.show(window);
                } else {
                    mController.show(window);
                }
            }
        });
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
    public boolean addListener(Object listener, @Nullable Type listenerClass) {
        return mController.addListener(listener, listenerClass);
    }

    @Override
    public boolean removeListener(Object listener, @Nullable Type listenerClass) {
        return mController.removeListener(listener, listenerClass);
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
    public void addStateListener(ILoadingStateListener listener) {
        mParentContainer.addStateListener(listener);
    }

    @Override
    public void removeStateListener(ILoadingStateListener listener) {
        mParentContainer.removeStateListener(listener);
    }

    @Override
    public Object getLoadingView() {
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
    public void checkPermissions(IPermissionResult permissionResult, String... permissions) {
        mParentContainer.checkPermissions(permissionResult, permissions);
    }

    @Override
    public void checkPermissions(IPermissionResult permissionResult, int requestCode, String... permissions) {
        mParentContainer.checkPermissions(permissionResult, requestCode, permissions);
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
    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
        return null;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        mController.onParamChanged(newParams);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        mController.onVisibleChanged(visible);
    }

    @Override
    public void onActivityChanged(boolean activity) {
        mController.onActivityChanged(activity);
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