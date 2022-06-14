package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnAttachListener;
import com.zpf.frame.IContainerHelper;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewState;
import com.zpf.support.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.support.util.StackAnimUtil;
import com.zpf.tool.StatusBarUtil;
import com.zpf.tool.expand.util.Logger;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.stack.AppStackUtil;
import com.zpf.views.window.ICustomWindowManager;

import java.lang.reflect.Type;

/**
 * 基于Activity的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public abstract class ContainerActivity extends Activity implements IViewContainer {
    protected final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private boolean isLauncher;
    private IViewProcessor mViewProcessor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        initTheme(intent.getIntExtra(AppConst.TARGET_VIEW_THEME, -1));
        String stackItemName = intent.getStringExtra(AppStackUtil.STACK_ITEM_NAME);
        if ((stackItemName == null || stackItemName.length() == 0) && mViewProcessor != null) {
            Class<?> cls = (Class<?>) intent.getSerializableExtra(AppConst.TARGET_VIEW_CLASS);
            if (cls != null) {
                stackItemName = cls.getName();
                intent.putExtra(AppStackUtil.STACK_ITEM_NAME, stackItemName);
            }
        }
        super.onCreate(savedInstanceState);
        //防止初次安装从后台返回的重启问题
        isLauncher = (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction()));
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 && isLauncher) {
            finish();
            return;
        }
        initWindow();
        mViewProcessor = initViewProcessor();
        if (mViewProcessor != null) {
            mController.add(mViewProcessor, null);
            mViewProcessor.initWindow(getWindow());
            setContentView(mViewProcessor.getView());
        } else {
            Logger.w("IViewProcessor is null!");
        }
        initView(savedInstanceState);
        mController.onCreate(savedInstanceState);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        mController.onRestart();
        onVisibleChanged(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mController.onStart();
        onVisibleChanged(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mController.onStop();
        onVisibleChanged(false);
    }

    @Override
    public void onDestroy() {
        mController.onDestroy();
        super.onDestroy();
        loadingManager = null;
    }

    @Override
    public void onBackPressed() {
        if (!mController.onInterceptBackPress() && !close()) {
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mController.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mController.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle oldParams = getIntent().getExtras();
        Bundle newParams = intent.getExtras();
        if (oldParams != null) {
            if (newParams != null) {
                oldParams.putAll(newParams);
            }
            mParams = oldParams;
        } else {
            mParams = newParams;
        }
        if (mParams != null) {
            intent.putExtras(mParams);
        }
        super.onNewIntent(intent);
        setIntent(intent);
        onParamChanged(mParams);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Intent getIntent() {
        return super.getIntent();
    }

    @Override
    public Activity getCurrentActivity() {
        return this;
    }

    @Override
    public ICustomWindowManager getCustomWindowManager() {
        return mController.getCustomWindowManager();
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        this.startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @NonNull OnActivityResultListener listener) {
        mController.addDisposable(listener, OnActivityResultListener.class);
        this.startActivityForResult(intent, intent.getIntExtra(AppConst.REQUEST_CODE, AppConst.DEF_REQUEST_CODE), null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        StackAnimUtil.onPush(this, intent.getIntExtra(AppConst.ANIM_TYPE, 0));
    }

    @Override
    public boolean close() {
        return loadingManager != null && loadingManager.hideLoading() || mController.getCustomWindowManager().close();
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
    public int size(@Nullable Type listenerClass) {
        return mController.size(listenerClass);
    }

    @Override
    public boolean hideLoading() {
        return loadingManager != null && loadingManager.hideLoading();
    }

    @Override
    public void setLoadingListener(OnAttachListener onAttachListener) {
        if (loadingManager != null) {
            loadingManager.setLoadingListener(onAttachListener);
        }
    }

    @Override
    public void showLoading() {
        showLoading(getString(R.string.support_request_loading));
    }

    @Override
    public void showLoading(Object message) {
        if (mController.isInteractive()) {
            if (loadingManager == null) {
                loadingManager = new LoadingManagerImpl(getContext());
            }
            loadingManager.showLoading(message);
        }
    }

    @Override
    public Object invoke(String name, Object params) {
        return null;
    }

    @Override
    public void setLoadingManager(ILoadingManager loadingManager) {
        this.loadingManager = loadingManager;
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
        if (isLauncher) {
            IContainerHelper containerHelper = CentralManager.getInstance(IContainerHelper.class);
            Class<?> launcherClass = null;
            if (containerHelper != null) {
                launcherClass = containerHelper.getLaunchProcessorClass(null);
            }
            if (launcherClass == null) {
                launcherClass = launcherViewProcessorClass();
            }
            if (launcherClass == null) {
                launcherClass = defViewProcessorClass();
            }
            if (launcherClass == null && containerHelper != null) {
                launcherClass = containerHelper.getErrorProcessorClass(null);
            }
            if (launcherClass != null) {
                mParams.putSerializable(AppConst.TARGET_VIEW_CLASS, launcherClass);
            }
        }
        return mParams;
    }

    @Override
    public int getContainerType() {
        return ContainerType.CONTAINER_ACTIVITY;
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
        Activity parentActivity = getParent();
        if (parentActivity instanceof IViewContainer) {
            return ((IViewContainer) parentActivity);
        }
        return null;
    }

    @Override
    @Nullable
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
    }

    @Override
    public IViewState getState() {
        return mController;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        mController.onParamChanged(newParams);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        mController.onVisibleChanged(visible);
    }

    protected void initTheme(int themeId) {
        if (themeId > 0) {
            setTheme(themeId);
        }
    }

    protected void initWindow() {
        try {
            setRequestedOrientation(getParams().getInt(AppConst.TARGET_VIEW_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        } catch (Exception e) {
            //
        }
        if (getParams().getBoolean(AppConst.TARGET_STATUS_TRANSLUCENT, true)) {
            StatusBarUtil.setStatusBarTranslucent(getWindow());
        }
    }

    protected IViewProcessor initViewProcessor() {
        return ContainerController.createViewProcessor(this, getParams(), defViewProcessorClass());
    }

    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    protected Class<? extends IViewProcessor> defViewProcessorClass() {
        return null;
    }

    protected Class<? extends IViewProcessor> launcherViewProcessorClass() {
        return null;
    }

}