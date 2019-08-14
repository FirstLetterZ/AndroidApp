package com.zpf.support.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IEvent;
import com.zpf.api.IManager;
import com.zpf.frame.IContainerHelper;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.INavigator;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.frame.IViewContainer;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.LifecycleState;
import com.zpf.tool.config.MainHandler;

/**
 * 基于AppCompatActivity的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class CompatContainerActivity extends AppCompatActivity implements IViewContainer {
    protected final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private IViewProcessor mViewProcessor;
    private boolean isLauncher;
    private IContainerHelper mHelper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止初次安装从后台返回的重启问题
        Intent intent = getIntent();
        isLauncher = (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction()));
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 && isLauncher) {
            finish();
            return;
        }
        initWindow();
        IViewProcessor viewProcessor = initViewProcessor();
        if (viewProcessor != null) {
            mController.addListener(viewProcessor);
            setContentView(viewProcessor.getView());
        } else {
            LogUtil.w("IViewProcessor is null!");
        }
        initView(savedInstanceState);
        mController.onCreate(savedInstanceState);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        mController.onRestart();
        mController.onVisibleChanged(true);
    }


    @Override
    public void onStart() {
        super.onStart();
        mController.onStart();
        mController.onVisibleChanged(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.onResume();
        mController.onActiviityChanged(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mController.onPause();
        mController.onActiviityChanged(false);
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
        super.onDestroy();
        loadingManager = null;
    }

    @Override
    public void onBackPressed() {
        if (!mController.onInterceptBackPress() && !dismiss()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            mController.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mController.onRestoreInstanceState(savedInstanceState);
        }
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
        mController.onParamChanged(mParams);
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
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
    }

    @Override
    public void startActivities(Intent[] intents) {
        super.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, @Nullable Bundle options) {
        super.startActivities(intents, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
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
    public boolean dismiss() {
        return loadingManager != null && loadingManager.hideLoading() || mController.dismiss();
    }

    @Override
    public IManager<ICancelable> getCancelableManager() {
        return mController.getCancelableManager();
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
        setResult(resultCode, data);
        super.finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean hideLoading() {
        return loadingManager != null && loadingManager.hideLoading();
    }

    @Override
    public View getLoadingView() {
        return loadingManager == null ? null : loadingManager.getLoadingView();
    }

    @Override
    public void showLoading() {
        showLoading(getString(R.string.default_request_loading));
    }

    @Override
    public void showLoading(Object message) {
        if (isLiving()) {
            if (loadingManager == null) {
                loadingManager = new LoadingManagerImpl(getContext());
            }
            loadingManager.showLoading(message);
        }
    }

    @Override
    public boolean checkPermissions(String... permissions) {
        return mController.getActivityPermissionChecker().checkPermissions(this, permissions);
    }

    @Override
    public boolean checkPermissions(int requestCode, String... permissions) {
        return mController.getActivityPermissionChecker().checkPermissions(this, requestCode, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, String... permissions) {
        mController.getActivityPermissionChecker().checkPermissions(this, onPermission, onLock, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, int requestCode, String... permissions) {
        mController.getActivityPermissionChecker().checkPermissions(this, onPermission, onLock, requestCode, permissions);
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
            Class launcherClass = null;
            if (mHelper != null) {
                launcherClass = mHelper.getLaunchProcessorClass(null);
            }
            if (launcherClass == null) {
                launcherClass = launcherViewProcessorClass();
            }
            if (launcherClass == null) {
                launcherClass = defViewProcessorClass();
            }
            if (launcherClass == null && mHelper != null) {
                launcherClass = mHelper.getErrorProcessorClass(null);
            }
            if (launcherClass != null) {
                mParams.putSerializable(AppConst.TARGET_VIEW_CLASS, launcherClass);
            }
        }
        if (mParams.getSerializable(AppConst.TARGET_VIEW_CLASS) == null) {
            mParams.putSerializable(AppConst.TARGET_VIEW_CLASS, defViewProcessorClass());
        }
        return mParams;
    }

    @Override
    public boolean sendEvenToView(@NonNull IEvent<Object> event) {
        if (mViewProcessor != null) {
            mViewProcessor.onReceiveEvent(event);
            return true;
        }
        return false;
    }

    @Override
    public int getContainerType() {
        return ContainerType.CONTAINER_COMPAT_ACTIVITY;
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
    public void bindView(IViewProcessor processor) {
        mViewProcessor = processor;
        if (mViewProcessor != null) {
            mController.addListener(mViewProcessor);
        }
    }

    @Override
    public void unbindView() {
        if (mViewProcessor != null) {
            mController.removeListener(mViewProcessor);
        }
        mViewProcessor = null;
    }

    @Override
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
    }

    @Override
    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
        return null;
    }

    protected void initWindow() {
        int themeId = getParams().getInt(AppConst.TARGET_VIEW_THEME, -1);
        if (themeId > 0) {
            setTheme(themeId);
        }
        setRequestedOrientation(getParams().getInt(AppConst.TARGET_VIEW_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        if (getParams().getBoolean(AppConst.TARGET_STATUS_TRANSLUCENT, true)) {
            setStatusBarTranslucent();
        }
    }

    protected void setStatusBarTranslucent() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//判断版本是5.0以上
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//判断版本是4.4以上
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected IViewProcessor initViewProcessor() {
        Class<? extends IViewProcessor> targetViewClass = null;
        IViewProcessor viewProcessor = null;
        try {
            targetViewClass = (Class<? extends IViewProcessor>) getParams().getSerializable(AppConst.TARGET_VIEW_CLASS);
        } catch (Exception e) {
            e.printStackTrace();
            if (mHelper != null) {
                targetViewClass = mHelper.getErrorProcessorClass(null);
            }
        }
        if (targetViewClass != null) {
            synchronized (ContainerController.class) {
                ContainerController.mInitingViewContainer = this;
                try {
                    viewProcessor = targetViewClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mHelper != null) {
                    targetViewClass = mHelper.getErrorProcessorClass(targetViewClass);
                }
                if (targetViewClass != null) {
                    try {
                        viewProcessor = targetViewClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ContainerController.mInitingViewContainer = null;
            }
        }
        return viewProcessor;
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
