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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zpf.api.ICallback;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.LifecycleListener;
import com.zpf.api.OnDestroyListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.ResultCallBackListener;
import com.zpf.support.constant.AppConst;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.config.LifecycleState;
import com.zpf.tool.config.MainHandler;

import java.lang.reflect.Constructor;

/**
 * 基于Activity的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class ContainerActivity extends Activity implements IViewContainer {
    private final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private IViewProcessor mViewProcessor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止初次安装从后台返回的重启问题
        Intent intent = getIntent();
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        initWindow();
        IViewProcessor viewProcessor = initViewProcessor();
        if (viewProcessor != null) {
            mController.addLifecycleListener(viewProcessor);
            mController.addResultCallBackListener(viewProcessor);
            setContentView(viewProcessor.getView());
        } else {
            LogUtil.w("IViewProcessor is null!");
        }
        mController.onPreCreate(savedInstanceState);
        initView(savedInstanceState);
        mController.afterCreate(savedInstanceState);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        mController.onRestart();
    }


    @Override
    public void onStart() {
        super.onStart();
        mController.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.onResume();
        mController.onVisibleChanged(true);
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
        if (mViewProcessor != null && mViewProcessor.onInterceptBackPress()) {
            return;
        }
        if (!dismiss()) {
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
        super.onNewIntent(intent);
        mController.onNewIntent(intent);
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
    public IManager<ICallback> getCallBackManager() {
        return mController.getCallBackManager();
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
    public void addLifecycleListener(LifecycleListener lifecycleListener) {
        mController.addLifecycleListener(lifecycleListener);
    }

    @Override
    public void removeLifecycleListener(LifecycleListener lifecycleListener) {
        mController.removeLifecycleListener(lifecycleListener);
    }

    @Override
    public void addOnDestroyListener(OnDestroyListener listener) {
        mController.addOnDestroyListener(listener);
    }

    @Override
    public void removeOnDestroyListener(OnDestroyListener listener) {
        mController.removeOnDestroyListener(listener);
    }

    @Override
    public void addResultCallBackListener(ResultCallBackListener callBackListener) {
        mController.addResultCallBackListener(callBackListener);
    }

    @Override
    public void removeResultCallBackListener(ResultCallBackListener callBackListener) {
        mController.removeResultCallBackListener(callBackListener);
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
        showLoading(null);
    }

    @Override
    public void showLoading(String message) {
        if (isLiving()) {
            if (loadingManager != null) {
                loadingManager = new LoadingManagerImpl(getContext());
            }
            if (loadingManager != null) {
                loadingManager.showLoading(message);
            }
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
    public boolean sendEvenToView(String action, Object... params) {
        if (mViewProcessor != null) {
            mViewProcessor.onReceiveEvent(action, params);
            return true;
        }
        return false;
    }

    @Override
    public int getContainerType() {
        return AppConst.CONTAINER_ACTIVITY;
    }

    @Override
    public IViewContainer getParentContainer() {
        Activity parentActivity = getParent();
        if (parentActivity != null && parentActivity instanceof IViewContainer) {
            return ((IViewContainer) parentActivity);
        }
        return null;
    }

    @Override
    public void bindView(IViewProcessor processor) {
        mViewProcessor = processor;
    }

    @Override
    public void unbindView() {
        mViewProcessor = null;
    }

    @Override
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
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
        IViewProcessor viewProcessor = null;
        Constructor<IViewProcessor> constructor = null;
        try {
            Class targetViewClass = (Class) getParams().getSerializable(AppConst.TARGET_VIEW_CLASS);
            if (targetViewClass != null) {
                constructor = targetViewClass.getConstructor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (ContainerController.class) {
            ContainerController.mInitingViewContainer = this;
            if (constructor != null) {
                try {
                    viewProcessor = constructor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (viewProcessor == null) {
                viewProcessor = unspecifiedViewProcessor();
            }
            ContainerController.mInitingViewContainer = null;
        }
        return viewProcessor;
    }

    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    protected IViewProcessor unspecifiedViewProcessor() {
        return null;
    }

}
