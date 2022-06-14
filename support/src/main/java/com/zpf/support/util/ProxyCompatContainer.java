package com.zpf.support.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnAttachListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewState;
import com.zpf.support.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.views.window.ICustomWindowManager;

import java.lang.reflect.Type;

/**
 * 将普通的activity或fragment打造成IViewContainer
 * Created by ZPF on 2018/6/28.
 * 不支持按键拦截 2021/2/1.
 */
public class ProxyCompatContainer extends Fragment implements IViewContainer {
    private FragmentActivity activity;
    private Fragment fragment;
    private ILoadingManager loadingManager;
    private boolean isVisible;
    private Bundle mParams;
    private final ContainerListenerController mController = new ContainerListenerController();

    public void onConditionsCompleted(FragmentActivity activity) {
        this.activity = activity;
    }

    public void onConditionsCompleted(Fragment fragment) {
        this.fragment = fragment;
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
    public ICustomWindowManager getCustomWindowManager() {
        return mController.getCustomWindowManager();
    }

    @Override
    public IViewState getState() {
        return mController;
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
        FragmentActivity activity = getActivity();
        if (activity instanceof IViewContainer) {
            return ((IViewContainer) activity).hideLoading();
        }
        return loadingManager != null && loadingManager.hideLoading() || mController.getCustomWindowManager().close();
    }

    @Override
    public void setLoadingListener(OnAttachListener onAttachListener) {
        if (loadingManager != null) {
            loadingManager.setLoadingListener(onAttachListener);
        }
    }

    @Override
    public void showLoading() {
        showLoading(getString(R.string.default_request_loading));
    }

    @Override
    public void showLoading(Object message) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof IViewContainer) {
                ((IViewContainer) activity).showLoading(message);
            } else if (mController.isLiving()) {
                if (loadingManager == null) {
                    loadingManager = new LoadingManagerImpl(getContext());
                }
                loadingManager.showLoading(message);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mController.onCreate(savedInstanceState);
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mController.onStart();
        checkVisibleChange(true);
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
        checkVisibleChange(false);
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
        loadingManager = null;
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
        checkVisibleChange(!hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkVisibleChange(isVisibleToUser);
    }

    @Override
    public Object invoke(String name, Object params) {
        return null;
    }

    public void setLoadingManager(ILoadingManager loadingManager) {
        this.loadingManager = loadingManager;
    }

    @NonNull
    @Override
    public Bundle getParams() {
        if (mParams == null) {
            mParams = getArguments();
        }
        if (mParams == null) {
            mParams = new Bundle();
        }
        return mParams;
    }

    @Override
    public int getContainerType() {
        return ContainerType.CONTAINER_COMPAT_FRAGMENT;
    }

    @Override
    public boolean setProcessorLinker(IViewLinker linker) {
        return false;
    }

    @Override
    public IViewContainer getParentContainer() {
        return null;
    }

    @Override
    public IViewProcessor getViewProcessor() {
        return null;
    }

    private void checkVisibleChange(boolean changeTo) {
        boolean newVisible = changeTo
                && FragmentHelper.checkFragmentVisible(this)
                && FragmentHelper.checkParentFragmentVisible(this);
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            onVisibleChanged(newVisible);
        }
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        mController.onParamChanged(newParams);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        mController.onVisibleChanged(visible);
    }
}