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

import com.zpf.api.ICallback;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.LifecycleListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.ResultCallBackListener;
import com.zpf.support.constant.AppConst;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.api.OnDestroyListener;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.config.LifecycleState;

import java.lang.reflect.Constructor;

/**
 * 基于android.support.v4.app.Fragment的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class CompatContainerFragment extends Fragment implements IViewContainer {
    private final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private boolean isVisible;
    private IViewProcessor mViewProcessor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = getView();
        if (theView == null) {
            IViewProcessor viewProcessor = initViewProcessor();
            if (viewProcessor != null) {
                theView = viewProcessor.getView();
            }
        }
        mController.onPreCreate(savedInstanceState);
        initView(savedInstanceState);
        mController.afterCreate(savedInstanceState);
        return theView;
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
        loadingManager = null;
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
    public void show(ICustomWindow window) {
        mController.show(window);
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
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof IViewContainer) {
            return ((IViewContainer) activity).hideLoading();
        }
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
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof IViewContainer) {
                ((IViewContainer) activity).showLoading(message);
            } else if (isLiving()) {
                if (loadingManager != null) {
                    loadingManager = new LoadingManagerImpl(getContext());
                }
                if (loadingManager != null) {
                    loadingManager.showLoading(message);
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

    @Override
    public void setLoadingManager(ILoadingManager loadingManager) {
        this.loadingManager = loadingManager;
    }

    @Override
    public Bundle getParams() {
        if (mParams == null) {
            mParams = getIntent().getExtras();
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
        return AppConst.CONTAINER_COMPAT_FRAGMENT;
    }

    @Override
    public IViewContainer getParentContainer() {
        android.support.v4.app.Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof IViewContainer) {
            return ((IViewContainer) parentFragment);
        }
        Activity parentActivity = getActivity();
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
    public void unbindView(IViewProcessor processor) {
        mViewProcessor = null;
    }

    private boolean checkParentFragmentVisible() {
        Fragment parent = getParentFragment();
        boolean result = true;
        while (parent != null) {
            result = checkFragmentShouldVisible(parent);
            if (result) {
                parent = parent.getParentFragment();
            } else {
                break;
            }
        }
        return result;
    }

    private boolean checkFragmentShouldVisible(Fragment fragment) {
        return fragment != null && fragment.getUserVisibleHint() && fragment.isAdded() && !fragment.isHidden();
    }

    private void checkVisibleChange() {
        boolean newVisible = getState() == LifecycleState.AFTER_RESUME
                && checkParentFragmentVisible()
                && checkFragmentShouldVisible(this);
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
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
        if (viewProcessor != null) {
            mController.addLifecycleListener(viewProcessor);
            mController.addResultCallBackListener(viewProcessor);
        } else {
            LogUtil.w("IViewProcessor is null!");
        }
        return viewProcessor;
    }

    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    protected IViewProcessor unspecifiedViewProcessor() {
        return null;
    }

}
