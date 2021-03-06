package com.zpf.support.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.IPermissionResult;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.ILoadingStateListener;
import com.zpf.frame.INavigator;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewContainer;
import com.zpf.support.R;
import com.zpf.support.constant.ContainerType;
import com.zpf.tool.permission.PermissionChecker;
import com.zpf.tool.stack.LifecycleState;

import java.lang.reflect.Type;

/**
 * 将普通的activity或fragment打造成IViewContainer
 * Created by ZPF on 2018/6/28.
 * 不支持按键拦截 2021/2/1.
 */
public class ProxyContainer extends Fragment implements IViewContainer {

    private Activity activity;
    private Fragment fragment;
    private ILoadingManager loadingManager;
    private boolean isVisible;
    private boolean isActivity;
    private Bundle mParams;
    private final ContainerListenerController mController = new ContainerListenerController();

    public void onConditionsCompleted(Activity activity) {
        this.activity = activity;
    }

    public void onConditionsCompleted(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    @LifecycleState
    public int getState() {
        return mController.getState();
    }

    @Override
    public boolean living() {
        return mController.living();
    }

    @Override
    public boolean interactive() {
        return mController.interactive();
    }

    @Override
    public boolean visible() {
        return mController.visible();
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
    public void show(ICustomWindow window) {
        mController.show(window);
    }

    @Override
    public boolean close() {
        return loadingManager != null && loadingManager.hideLoading() || mController.close();
    }

    @Override
    public IManager<ICancelable> getCancelableManager() {
        return mController.getCancelableManager();
    }

    @Override
    public boolean addListener(Object listener, @Nullable Type listenerClass) {
        return mController.addListener(listener, listenerClass);
    }

    @Override
    public boolean removeListener(Object listener, @Nullable Type listenerClass) {
        return mController.removeListener(listener, listenerClass);
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
    public boolean hideLoading() {
        Activity activity = getActivity();
        if (activity instanceof IViewContainer) {
            return ((IViewContainer) activity).hideLoading();
        }
        return loadingManager != null && loadingManager.hideLoading() || mController.close();
    }

    @Override
    public void addStateListener(ILoadingStateListener listener) {
        if (loadingManager != null) {
            loadingManager.addStateListener(listener);
        }
    }

    @Override
    public void removeStateListener(ILoadingStateListener listener) {
        if (loadingManager != null) {
            loadingManager.removeStateListener(listener);
        }
    }

    @Override
    public Object getLoadingView() {
        return loadingManager == null ? null : loadingManager.getLoadingView();
    }

    @Override
    public void showLoading() {
        showLoading(getString(R.string.default_request_loading));
    }

    @Override
    public void showLoading(Object message) {
        Activity activity = getActivity();
        if (activity != null) {
            if (activity instanceof IViewContainer) {
                ((IViewContainer) activity).showLoading(message);
            } else if (living()) {
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
        checkActivity(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mController.onPause();
        checkActivity(false);
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
    public boolean checkPermissions(String... permissions) {
        return mController.getFragmentPermissionChecker().checkPermissions(this, permissions);
    }

    @Override
    public boolean checkPermissions(int requestCode, String... permissions) {
        return mController.getFragmentPermissionChecker().checkPermissions(this, requestCode, permissions);
    }

    @Override
    public void checkPermissions(IPermissionResult permissionResult, String... permissions) {
        mController.getFragmentPermissionChecker().checkPermissions(
                this, PermissionChecker.REQ_PERMISSION_CODE, permissionResult, permissions);
    }

    @Override
    public void checkPermissions(IPermissionResult permissionResult, int requestCode, String... permissions) {
        mController.getFragmentPermissionChecker().checkPermissions(
                this, requestCode, permissionResult, permissions);
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
        return ContainerType.CONTAINER_FRAGMENT;
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

    @Override
    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
        return null;
    }

    private void checkVisibleChange(boolean changeTo) {
        boolean newVisible = changeTo
                && FragmentHelper.checkFragmentVisible(this)
                && FragmentHelper.checkParentFragmentVisible(this);
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
            checkActivity(mController.interactive());
        }
    }

    private void checkActivity(boolean changeTo) {
        if (!isVisible) {
            changeTo = false;
        }
        if (isActivity != changeTo) {
            isActivity = changeTo;
            mController.onActivityChanged(changeTo);
        }
    }
}