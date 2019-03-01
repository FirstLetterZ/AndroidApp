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
import com.zpf.tool.config.LifecycleState;
import com.zpf.tool.config.MainHandler;

import java.lang.reflect.Constructor;

/**
 * 基于Activity的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public abstract class ActivityContainer extends Activity implements IViewContainer {
    private final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingDialog;

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
        Class targetViewClass = null;
        IViewProcessor viewProcessor = null;
        synchronized (ContainerController.class) {
            ContainerController.mInitingViewContainer = this;
            try {
                targetViewClass = (Class) intent.getSerializableExtra(AppConst.TARGET_VIEW_CLASS);
                Constructor<IViewProcessor> constructor = targetViewClass.getConstructor();
                viewProcessor = constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ContainerController.mInitingViewContainer = null;
        }
        if (viewProcessor != null) {
            mController.addLifecycleListener(viewProcessor);
            mController.addResultCallBackListener(viewProcessor);
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
        loadingDialog = null;
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
        return loadingDialog != null && loadingDialog.hideLoading() || mController.dismiss();
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
        return loadingDialog != null && loadingDialog.hideLoading();
    }

    @Override
    public void showLoading() {
        showLoading(null);
    }

    @Override
    public void showLoading(String message) {
        if (isLiving()) {
            if (loadingDialog != null) {
                loadingDialog = createLoadingManager();
            }
            if (loadingDialog != null) {
                loadingDialog.showLoading(message);
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


    protected void initWindow() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setStatusBarTranslucent();
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

    public abstract void initView(@Nullable Bundle savedInstanceState);

    public abstract ILoadingManager createLoadingManager();
}
