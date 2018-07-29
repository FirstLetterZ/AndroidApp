package com.zpf.support.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.BaseKeyConst;
import com.zpf.support.defview.ProgressDialog;
import com.zpf.support.defview.RootLayout;
import com.zpf.support.util.CacheMap;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LifecycleLogUtil;
import com.zpf.support.util.PermissionUtil;
import com.zpf.support.util.PublicUtil;
import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.LifecycleInterface;
import com.zpf.support.interfaces.OnDestroyListener;
import com.zpf.support.interfaces.ResultCallBackListener;
import com.zpf.support.interfaces.RootLayoutInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.interfaces.ViewContainerInterface;
import com.zpf.support.interfaces.ViewInterface;
import com.zpf.support.interfaces.constant.LifecycleState;

import java.lang.reflect.Constructor;

/**
 * Created by ZPF on 2018/6/14.
 */
public abstract class BaseActivity<T extends ViewInterface> extends AppCompatActivity implements ViewContainerInterface {
    protected T mView;
    private RootLayoutInterface mRootLayout;
    private final ContainerListenerController mController = new ContainerListenerController();
    private ProgressDialog loadingDialog;

    protected void initWindow() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setStatusBarTranslucent();
    }

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
        if(CacheMap.getBoolean(BaseKeyConst.IS_DEBUG)){
            new LifecycleLogUtil(this);
        }
        mRootLayout = new RootLayout(getContext());
        View layoutView = getLayoutView();
        if (layoutView == null) {
            mRootLayout.setContentView(getLayoutInflater(), getLayoutId());
        } else {
            mRootLayout.setContentView(layoutView);
        }
        setContentView(mRootLayout.getLayout());
        try {
            Class<T> cls = PublicUtil.getViewClass(getClass());
            if (cls != null) {
                Class[] pType = new Class[]{ViewContainerInterface.class};
                Constructor<T> constructor = cls.getConstructor(pType);
                mView = constructor.newInstance(this);
                mController.addLifecycleListener(mView);
                mController.addResultCallBackListener(mView);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        mController.onPreCreate(savedInstanceState);
        initView(savedInstanceState);
        mController.afterCreate(savedInstanceState);
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
        loadingDialog = null;
        super.onDestroy();
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
        PermissionUtil.get().onRequestPermissionsResult(this, permissions, grantResults);
        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    @LifecycleState
    public int getState() {
        return mController.getState();
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
    public RootLayoutInterface getRootLayout() {
        return mRootLayout;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void show(SafeWindowInterface window) {
        mController.show(window);
    }


    @Override
    public CallBackManagerInterface getCallBackManager() {
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
    public void addLifecycleListener(LifecycleInterface lifecycleListener) {
        mController.addLifecycleListener(lifecycleListener);
    }

    @Override
    public void removeLifecycleListener(LifecycleInterface lifecycleListener) {
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
        boolean result;
        if (loadingDialog != null && loadingDialog.isShowing()) {
            result = true;
            loadingDialog.dismiss();
        } else {
            result = mController.dismiss();
        }
        return result;
    }

    @Override
    public void showLoading() {
        showLoading(AppConst.PROGRESS_WAITTING);
    }

    @Override
    public void showLoading(String message) {
        if (loadingDialog != null) {
            loadingDialog = getProgressDialog();
        }
        if (loadingDialog != null) {
            loadingDialog.setText(message);
        }
        show(loadingDialog);
    }

    @Override
    public void onBackPressed() {
        if (!hideLoading()) {
            super.onBackPressed();
        }
    }

    protected ProgressDialog getProgressDialog() {
        if (loadingDialog == null && getState() < LifecycleState.AFTER_DESTROY) {
            loadingDialog = new ProgressDialog(this);
        }
        return loadingDialog;
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

    public View getLayoutView() {
        return null;
    }

    public abstract int getLayoutId();

    public abstract void initView(@Nullable Bundle savedInstanceState);

}
