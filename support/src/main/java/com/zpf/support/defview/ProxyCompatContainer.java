package com.zpf.support.defview;

import android.annotation.SuppressLint;
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
import com.zpf.support.util.ContainerListenerController;
import com.zpf.api.CallBackManagerInterface;
import com.zpf.api.LifecycleInterface;
import com.zpf.api.OnDestroyListener;
import com.zpf.api.ResultCallBackListener;
import com.zpf.api.RootLayoutInterface;
import com.zpf.api.SafeWindowInterface;
import com.zpf.api.TitleBarInterface;
import com.zpf.api.ViewContainerInterface;
import com.zpf.support.util.LifecycleLogUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.LifecycleState;

/**
 * 将普通的activity或fragment打造成ViewContainerInterface
 * Created by ZPF on 2018/6/28.
 */
public class ProxyCompatContainer extends Fragment implements ViewContainerInterface {
    private FragmentActivity activity;
    private Fragment fragment;
    private ProgressDialog loadingDialog;
    private boolean isVisible;
    private final ContainerListenerController mController = new ContainerListenerController();

    public void onConditionsCompleted(FragmentActivity activity) {
        this.activity = activity;
        if (GlobalConfigImpl.get().isDebug() && activity != null) {
            LifecycleLogUtil lifecycleLogUtil = new LifecycleLogUtil(this);
            lifecycleLogUtil.setName(activity.getClass().getName());
        }
    }

    public void onConditionsCompleted(Fragment fragment) {
        this.fragment = fragment;
        if (GlobalConfigImpl.get().isDebug() && fragment != null) {
            LifecycleLogUtil lifecycleLogUtil = new LifecycleLogUtil(this);
            lifecycleLogUtil.setName(fragment.getClass().getName());
        }
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
        if (activity != null) {
            return activity;
        } else if (fragment != null) {
            return fragment.getContext();
        } else {
            return null;
        }
    }

    @Override
    public Intent getIntent() {
        if (activity != null) {
            return activity.getIntent();
        } else if (fragment != null && fragment.getActivity() != null) {
            return fragment.getActivity().getIntent();
        } else {
            return null;
        }
    }

    @Override
    public Activity getCurrentActivity() {
        if (activity != null) {
            return activity;
        } else if (fragment != null) {
            return fragment.getActivity();
        } else {
            return null;
        }
    }

    @Override
    public RootLayoutInterface getRootLayout() {
        return null;
    }

    @Override
    public TitleBarInterface getTitleBar() {
        return null;
    }

    @Override
    public void startActivity(Intent intent) {
        if (activity != null) {
            activity.startActivity(intent);
        } else if (fragment != null) {
            fragment.startActivity(intent);
        }
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        if (activity != null) {
            activity.startActivity(intent, options);
        } else if (fragment != null) {
            fragment.startActivity(intent, options);
        }
    }

    @Override
    public void startActivities(Intent[] intents) {
        if (activity != null) {
            activity.startActivities(intents);
        } else if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().startActivities(intents);
        }
    }

    @Override
    public void startActivities(Intent[] intents, @Nullable Bundle options) {
        if (activity != null) {
            activity.startActivities(intents, options);
        } else if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().startActivities(intents, options);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode, options);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode, options);
        }
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
        if (activity != null) {
            activity.setResult(resultCode, data);
            activity.finish();
        } else if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().setResult(resultCode, data);
            fragment.getActivity().finish();
        }
    }

    @Override
    public void finish() {
        if (activity != null) {
            activity.finish();
        } else if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().finish();
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
    public ProgressDialog getProgressDialog() {
        if (loadingDialog == null && getState() < LifecycleState.AFTER_DESTROY && getActivity() != null) {
            if (activity != null) {
                loadingDialog = new ProgressDialog(activity);
            } else if (fragment != null && fragment.getActivity() != null) {
                loadingDialog = new ProgressDialog(fragment.getActivity());
            }
        }
        return loadingDialog;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mController.afterCreate(savedInstanceState);
        return null;
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
    public void onDestroy() {
        mController.onDestroy();
        loadingDialog = null;
        activity = null;
        fragment = null;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    private void checkVisibleChange() {
        boolean newVisible = getState() == LifecycleState.AFTER_RESUME && getUserVisibleHint() && isVisible();
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
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
}
