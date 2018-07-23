package com.zpf.baselib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.zpf.baselib.interfaces.LifecycleInterface;
import com.zpf.baselib.interfaces.OnDestroyListener;
import com.zpf.baselib.interfaces.ResultCallBackListener;
import com.zpf.baselib.interfaces.RootLayoutInterface;
import com.zpf.baselib.interfaces.SafeWindowInterface;
import com.zpf.baselib.interfaces.ViewContainerInterface;
import com.zpf.baselib.util.CallBackManager;
import com.zpf.baselib.util.LifecycleLogUtil;
import com.zpf.baselib.util.LoadingUtil;
import com.zpf.baselib.util.PublicUtil;

/**
 * 将普通的activity或fragment打造成ViewContainerInterface
 * Created by ZPF on 2018/6/28.
 */
public class ProxyViewContainer implements ViewContainerInterface, LifecycleInterface {
    private Activity activity;
    private Fragment fragment;
    private LoadingUtil loadingUtil;
    private RootLayout rootLayout;
    private final ContainerListenerController mController = new ContainerListenerController();

    public ProxyViewContainer(Activity activity, RootLayout rootLayout) {
        onConditionsCompleted(activity, null, rootLayout);

    }

    public ProxyViewContainer(Fragment fragment, RootLayout rootLayout) {
        onConditionsCompleted(null, fragment, rootLayout);
    }

    private void onConditionsCompleted(Activity activity, Fragment fragment, RootLayout rootLayout) {
        if (activity != null && activity instanceof BaseActivity) {
            this.activity = null;
        } else {
            this.activity = activity;
        }
        if (fragment != null && fragment instanceof BaseFragment) {
            this.fragment = null;
        } else {
            this.fragment = fragment;
        }
        this.rootLayout = rootLayout;
        if (this.activity != null) {
            loadingUtil = new LoadingUtil(activity);
        } else if (this.fragment != null && rootLayout != null) {
            rootLayout.getStatusBar().setVisibility(View.GONE);
            rootLayout.getTitleBar().getLayout().setVisibility(View.GONE);
        }
    }

    @Override
    public int getState() {
        return mController.getState();
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
    public RootLayoutInterface getRootLayout() {
        return rootLayout;
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
    public CallBackManager getCallBackManager() {
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
        boolean result = false;
        if (activity != null) {
            if (loadingUtil != null && loadingUtil.isLoading()) {
                result = true;
                loadingUtil.hideLoading();
            } else {
                result = mController.dismiss();
            }
        } else if (fragment != null) {
            Activity activity = fragment.getActivity();
            if (activity != null && activity instanceof BaseActivity) {
                result = ((BaseActivity) activity).hideLoading();
            } else {
                result = mController.dismiss();
            }
        }
        return result;
    }

    @Override
    public void showLoading() {
        if (loadingUtil != null) {
            loadingUtil.showLoading();
        } else if (fragment != null) {
            Activity activity = fragment.getActivity();
            if (activity != null && activity instanceof BaseActivity) {
                ((BaseActivity) activity).showLoading();
            }
        }
    }

    @Override
    public void showLoading(String message) {
        if (loadingUtil != null) {
            loadingUtil.showLoading(message);
        } else if (fragment != null) {
            Activity activity = fragment.getActivity();
            if (activity != null && activity instanceof BaseActivity) {
                ((BaseActivity) activity).showLoading(message);
            }
        }
    }

    @Override
    public void onPreCreate(@Nullable Bundle savedInstanceState) {
        if (PublicUtil.isDebug()) {
            new LifecycleLogUtil(this);
        }
        mController.onPreCreate(savedInstanceState);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        mController.afterCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        mController.onStart();
    }

    @Override
    public void onResume() {
        mController.onResume();
    }

    @Override
    public void onPause() {
        mController.onPause();
    }

    @Override
    public void onStop() {
        mController.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mController.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mController.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mController.onDestroy();
        loadingUtil = null;
        activity = null;
        fragment = null;
    }

}
