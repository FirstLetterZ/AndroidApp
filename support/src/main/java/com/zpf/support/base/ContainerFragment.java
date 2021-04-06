package com.zpf.support.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.IPermissionResult;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnAttachListener;
import com.zpf.api.OnTouchKeyListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.INavigator;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewStateListener;
import com.zpf.support.R;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.single.base.CompatSinglePageActivity;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.util.FragmentHelper;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.tool.expand.util.LogUtil;
import com.zpf.tool.permission.PermissionChecker;
import com.zpf.tool.stack.LifecycleState;

import java.lang.reflect.Type;

/**
 * 基于android.app.Fragment的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class ContainerFragment extends Fragment implements IViewContainer, IViewStateListener, OnActivityResultListener {
    protected final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private boolean isVisible;
    private boolean isActivity;
    private IViewProcessor mViewProcessor;
    private final IBackPressInterceptor backPressInterceptor = new IBackPressInterceptor() {
        @Override
        public boolean onInterceptBackPress() {
            return isActivity && (close() || mController.onInterceptBackPress());
        }
    };
    private final OnTouchKeyListener touchKeyListener = new OnTouchKeyListener() {

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            return isActivity && mController.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            return isActivity && mController.onKeyUp(keyCode, event);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View theView = getView();
        if (mViewProcessor == null) {
            mViewProcessor = initViewProcessor();
            if (mViewProcessor != null) {
                mController.addListener(mViewProcessor, null);
                theView = mViewProcessor.getView();
            } else {
                LogUtil.w("IViewProcessor is null!");
            }
        }
        IViewContainer parentContainer = getParentContainer();
        if (parentContainer != null) {
            parentContainer.addListener(backPressInterceptor, IBackPressInterceptor.class);
            parentContainer.addListener(touchKeyListener, OnTouchKeyListener.class);
        }
        initView(savedInstanceState);
        return theView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mController.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mController.onStart();
        checkVisibleChange(true, false);
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
        checkVisibleChange(false, false);
    }

    @Override
    public void onDestroyView() {
        checkToDestroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        checkToDestroy();
        super.onDestroy();
        loadingManager = null;
    }

    private void checkToDestroy() {
        if (mController.getState() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
            IViewContainer parentContainer = getParentContainer();
            if (parentContainer != null) {
                parentContainer.removeListener(backPressInterceptor, IBackPressInterceptor.class);
                parentContainer.removeListener(touchKeyListener, OnTouchKeyListener.class);
            }
        }
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
        checkVisibleChange(!hidden, true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkVisibleChange(isVisibleToUser, true);
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

    @Override
    public void setLoadingManager(ILoadingManager loadingManager) {
        this.loadingManager = loadingManager;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        Bundle oldParams = getArguments();
        if (oldParams != null) {
            if (args != null) {
                oldParams.putAll(args);
            }
            mParams = oldParams;
        } else {
            mParams = args;
        }
        try {
            super.setArguments(mParams);
        } catch (Exception e) {
            //
        }
        if (isAdded()) {
            onParamChanged(mParams);
        }
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
    public int getContainerType() {
        if (getActivity() instanceof CompatSinglePageActivity) {
            return ContainerType.CONTAINER_SINGLE_FRAGMENT;
        }
        return ContainerType.CONTAINER_FRAGMENT;
    }

    @Override
    public boolean setProcessorLinker(IViewLinker linker) {
        try {
            mViewProcessor.onReceiveLinker(linker);
            return true;
        } catch (Exception e) {
            return false;
        }
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
    public IViewProcessor getViewProcessor() {
        return mViewProcessor;
    }

    @Override
    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
        Activity parentActivity = getActivity();
        if (parentActivity instanceof IViewContainer) {
            return ((IViewContainer) parentActivity).getNavigator();
        }
        return null;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        mController.onParamChanged(newParams);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        mController.onVisibleChanged(visible);
    }

    @Override
    public void onActivityChanged(boolean activity) {
        mController.onActivityChanged(activity);
    }

    private void checkVisibleChange(boolean changeTo, boolean notifyChildren) {
        boolean newVisible = changeTo
                && FragmentHelper.checkFragmentVisible(this)
                && FragmentHelper.checkParentFragmentVisible(this);
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            onVisibleChanged(newVisible);
            if (notifyChildren) {
                FragmentHelper.notifyChildrenFragmentVisible(this, newVisible);
            }
            checkActivity(mController.interactive());
        }
    }

    private void checkActivity(boolean changeTo) {
        if (!isVisible) {
            changeTo = false;
        }
        if (isActivity != changeTo) {
            isActivity = changeTo;
            onActivityChanged(changeTo);
        }
    }

    protected IViewProcessor initViewProcessor() {
        return ContainerController.createViewProcessor(this, getParams(), null);
    }

    protected void initView(@Nullable Bundle savedInstanceState) {

    }

}