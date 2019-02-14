package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.support.constant.AppConst;
import com.zpf.support.defview.ProgressDialog;
import com.zpf.support.defview.RootLayout;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.tool.PublicUtil;
import com.zpf.api.TitleBarInterface;
import com.zpf.api.CallBackManagerInterface;
import com.zpf.api.LifecycleInterface;
import com.zpf.api.OnDestroyListener;
import com.zpf.api.ResultCallBackListener;
import com.zpf.api.RootLayoutInterface;
import com.zpf.api.SafeWindowInterface;
import com.zpf.api.ViewContainerInterface;
import com.zpf.api.ContainerProcessorInterface;
import com.zpf.tool.config.LifecycleState;

import java.lang.reflect.Constructor;

/**
 * 基于android.support.v4.app.Fragment的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public abstract class CompatFragmentContainer<T extends ContainerProcessorInterface> extends Fragment implements ViewContainerInterface {
    protected T mView;
    private RootLayoutInterface mRootLayout;
    private boolean isVisible;
    private ProgressDialog loadingDialog;
    private final ContainerListenerController mController = new ContainerListenerController();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootLayout == null) {
            mRootLayout = createRootLayout();
            View layoutView = getLayoutView();
            if (layoutView == null) {
                mRootLayout.setContentView(inflater, getLayoutId());
            } else {
                mRootLayout.setContentView(layoutView);
            }
        }
        if (mView == null) {
            mView = createProcessor();
        }
        if (mView != null) {
            mController.addLifecycleListener(mView);
            mController.addResultCallBackListener(mView);
        }
        mController.onPreCreate(savedInstanceState);
        initView(savedInstanceState);
        mController.afterCreate(savedInstanceState);
        return mRootLayout.getLayout();
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
        checkVisibleChange();
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
        checkVisibleChange();
    }


    @Override
    public void onDestroyView() {
        if (mController.getState() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mController.getState() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
        }
        super.onDestroy();
        loadingDialog = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mController.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mController.onRestoreInstanceState(savedInstanceState);
        }
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        checkVisibleChange();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkVisibleChange();
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
        return super.getContext();
    }

    @Override
    public Intent getIntent() {
        if (getActivity() != null) {
            return getActivity().getIntent();
        } else {
            return null;
        }
    }

    @Override
    public Activity getCurrentActivity() {
        return getActivity();
    }

    @Override
    public RootLayoutInterface getRootLayout() {
        return mRootLayout;
    }

    @Override
    public TitleBarInterface getTitleBar() {
        if (mRootLayout != null) {
            return mRootLayout.getTitleBar();
        } else {
            return null;
        }
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
        this.startActivities(intents, null);
    }

    @Override
    public void startActivities(Intent[] intents, @Nullable Bundle options) {
        if (getActivity() != null) {
            getActivity().startActivities(intents, options);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void show(SafeWindowInterface window) {
        mController.show(window);
    }

    @Override
    public boolean dismiss() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return mController.dismiss();
        }
    }

    @Override
    public CallBackManagerInterface getCallBackManager() {
        return mController.getCallBackManager();
    }

    @Override
    public void finishWithResult(int resultCode, Intent data) {
        if (getActivity() != null) {
            getActivity().setResult(resultCode, data);
            getActivity().finish();
        }
    }

    @Override
    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
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
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof ViewContainerInterface) {
            return ((ViewContainerInterface) activity).hideLoading();
        } else if (loadingDialog != null && loadingDialog.isShowing()) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ProgressDialog getProgressDialog() {
        if (loadingDialog == null && getState() < LifecycleState.AFTER_DESTROY && getActivity() != null) {
            loadingDialog = new ProgressDialog(getActivity());
        }
        return loadingDialog;
    }

    @Override
    public void showLoading() {
        showLoading(AppConst.PROGRESS_WAITTING);
    }

    @Override
    public void showLoading(String message) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof ViewContainerInterface) {
                ((ViewContainerInterface) activity).showLoading(message);
            } else if (isLiving()) {
                if (loadingDialog == null) {
                    loadingDialog = getProgressDialog();
                }
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setText(message);
                    loadingDialog.show();
                }
            }
        }
    }

    @Override
    public boolean checkPermissions(String... permissions) {
        return mController.getSupportFragmentPermissionChecker().checkPermissions(this, permissions);
    }

    @Override
    public boolean checkPermissions(int requestCode, String... permissions) {
        return mController.getSupportFragmentPermissionChecker().checkPermissions(this, requestCode, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, String... permissions) {
        mController.getSupportFragmentPermissionChecker().checkPermissions(this, onPermission, onLock, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, int requestCode, String... permissions) {
        mController.getSupportFragmentPermissionChecker().checkPermissions(this, onPermission, onLock, requestCode, permissions);
    }

    @Override
    public Object invoke(String name, Object params) {
        return null;
    }

    protected RootLayoutInterface createRootLayout() {
        RootLayout rootLayout = new RootLayout(getContext());
        rootLayout.getStatusBar().setVisibility(View.GONE);
        rootLayout.getTitleBar().getLayout().setVisibility(View.GONE);
        return rootLayout;
    }

    protected T createProcessor() {
        Class<T> cls = PublicUtil.getGenericClass(getClass());
        if (cls != null) {
            try {
                Class[] pType = new Class[]{ViewContainerInterface.class};
                Constructor<T> constructor = cls.getConstructor(pType);
                return constructor.newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public View getLayoutView() {
        return null;
    }

    public abstract int getLayoutId();

    public abstract void initView(@Nullable Bundle savedInstanceState);

    private void checkVisibleChange() {
        boolean newVisible = getState() == LifecycleState.AFTER_RESUME && getUserVisibleHint() && isVisible();
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
        }
    }

}
