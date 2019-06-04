package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewContainer;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.FragmentHelper;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.config.LifecycleState;

import java.lang.reflect.Constructor;

/**
 * 基于android.app.Fragment的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class ContainerFragment extends Fragment implements IViewContainer, IBackPressInterceptor {
    private final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private boolean isVisible;
    private boolean isActivity;
    private IViewProcessor mViewProcessor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = getView();
        if (theView == null) {
            IViewProcessor viewProcessor = initViewProcessor();
            if (viewProcessor != null) {
                theView = viewProcessor.getView();
            } else {
                LogUtil.w("IViewProcessor is null!");
            }
        }
        initView(savedInstanceState);
        return theView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        IViewContainer parentContainer = getParentContainer();
        if (parentContainer != null) {
            parentContainer.addListener(this);
        }
        mController.onCreate(savedInstanceState);
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
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            mController.onSaveInstanceState(outState);
        }
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
        checkVisibleChange(!hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkVisibleChange(isVisibleToUser);
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
    public IManager<ICancelable> getCancelableManager() {
        return mController.getCancelableManager();
    }

    @Override
    public boolean addListener(Object listener) {
        return mController.addListener(listener);
    }

    @Override
    public boolean removeListener(Object listener) {
        return false;
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
    public boolean hideLoading() {
        Activity activity = getActivity();
        if (activity instanceof IViewContainer) {
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
    public void showLoading(Object message) {
        Activity activity = getActivity();
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
        return mController.getFragmentPermissionChecker().checkPermissions(this, permissions);
    }

    @Override
    public boolean checkPermissions(int requestCode, String... permissions) {
        return mController.getFragmentPermissionChecker().checkPermissions(this, requestCode, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, String... permissions) {
        mController.getFragmentPermissionChecker().checkPermissions(this, onPermission, onLock, permissions);
    }

    @Override
    public void checkPermissions(Runnable onPermission, Runnable onLock, int requestCode, String... permissions) {
        mController.getFragmentPermissionChecker().checkPermissions(this, onPermission, onLock, requestCode, permissions);
    }

    @Override
    public boolean onInterceptBackPress() {
        return mController.onInterceptBackPress();
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
    public void setArguments(@Nullable Bundle args) {
        if (isAdded() && args != getArguments()) {
            mController.onParamChanged(args);
        }
        mParams = args;
    }

    @NonNull
    @Override
    public Bundle getParams() {
        if (mParams == null) {
            mParams = getArguments();
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
        return ContainerType.CONTAINER_FRAGMENT;
    }

    @Override
    public IViewContainer getParentContainer() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof IViewContainer) {
            return ((IViewContainer) parentFragment);
        }
        Activity parentActivity = getActivity();
        if (parentActivity instanceof IViewContainer) {
            return ((IViewContainer) parentActivity);
        }
        return null;
    }

    @Override
    public void bindView(IViewProcessor processor) {
        mViewProcessor = processor;
        if (mViewProcessor != null) {
            mController.addListener(mViewProcessor);
        }
    }

    @Override
    public void unbindView() {
        if (mViewProcessor != null) {
            mController.removeListener(mViewProcessor);
        }
        mViewProcessor = null;
    }

    @Override
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
    }


    private void checkVisibleChange(boolean changeTo) {
        boolean newVisible = changeTo
                && FragmentHelper.checkFragmentVisible(this)
                && FragmentHelper.checkParentFragmentVisible(this);
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
            if (!isVisible && isActivity) {
                isActivity = false;
                mController.onActiviityChanged(false);
            }
        }
    }

    private void checkActivity(boolean changeTo) {
        if (!isVisible) {
            changeTo = false;
        }
        if (isActivity != changeTo) {
            isActivity = changeTo;
            mController.onActiviityChanged(changeTo);
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
