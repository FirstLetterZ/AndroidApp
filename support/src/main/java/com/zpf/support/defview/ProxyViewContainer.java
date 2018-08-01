package com.zpf.support.defview;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.support.constant.BaseKeyConst;
import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.LifecycleInterface;
import com.zpf.support.interfaces.OnDestroyListener;
import com.zpf.support.interfaces.ResultCallBackListener;
import com.zpf.support.interfaces.RootLayoutInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.interfaces.ViewContainerInterface;
import com.zpf.support.interfaces.constant.LifecycleState;
import com.zpf.support.util.CacheMap;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LifecycleLogUtil;
import com.zpf.support.util.PermissionUtil;

/**
 * 将普通的activity或fragment打造成ViewContainerInterface
 * Created by ZPF on 2018/6/28.
 */
public class ProxyViewContainer extends Fragment implements ViewContainerInterface {
    private Activity activity;
    private Fragment fragment;
    private ProgressDialog loadingDialog;
    private boolean isVisible;
    private final ContainerListenerController mController = new ContainerListenerController();

    public void onConditionsCompleted(Activity activity) {
        this.activity = activity;
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG) && activity != null) {
            LifecycleLogUtil lifecycleLogUtil = new LifecycleLogUtil(this);
            lifecycleLogUtil.setName(activity.getClass().getName());
        }

    }

    public void onConditionsCompleted(Fragment fragment) {
        this.fragment = fragment;
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG) && fragment != null) {
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
    public Context getContext() {
        if (activity != null) {
            return activity;
        } else if (fragment != null) {
            return fragment.getActivity();
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
    public RootLayoutInterface getRootLayout() {
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
    public void startActivityForResult(Intent intent, int requestCode) {
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        }
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
        if (loadingDialog == null) {
            loadingDialog = createProgressDialog();
        }
        show(loadingDialog);
    }

    @Override
    public void showLoading(String message) {
        if (getState() < LifecycleState.AFTER_DESTROY) {
            if (loadingDialog != null) {
                loadingDialog = createProgressDialog();
            }
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setText(message);
                loadingDialog.show();
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
    public void onDestroyView() {
        if (mController.getState() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
            loadingDialog = null;
            activity = null;
            fragment = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mController.getState() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
            loadingDialog = null;
            activity = null;
            fragment = null;
        }
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
        PermissionUtil.get().onRequestPermissionsResult(this, permissions, grantResults);
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

    protected ProgressDialog createProgressDialog() {
        if (activity != null) {
            return new ProgressDialog(activity);
        } else if (fragment != null && fragment.getActivity() != null) {
            return new ProgressDialog(fragment.getActivity());
        }
        return null;
    }

    private void checkVisibleChange() {
        boolean newVisible = isVisible();
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
        }
    }
}
