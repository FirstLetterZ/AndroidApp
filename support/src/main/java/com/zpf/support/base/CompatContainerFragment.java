package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnAttachListener;
import com.zpf.api.OnTouchKeyListener;
import com.zpf.frame.ILoadingManager;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewState;
import com.zpf.support.R;
import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.ContainerType;
import com.zpf.support.model.CompatFragmentPerChecker;
import com.zpf.support.single.base.CompatSinglePageActivity;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.FragmentHelper;
import com.zpf.support.util.LoadingManagerImpl;
import com.zpf.support.util.StackAnimUtil;
import com.zpf.tool.expand.util.Logger;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.stack.LifecycleState;
import com.zpf.views.window.ICustomWindowManager;

import java.lang.reflect.Type;

/**
 * 基于androidx.fragment.app.Fragment的视图容器层
 * Created by ZPF on 2018/6/14.
 */
public class CompatContainerFragment extends Fragment implements IViewContainer {
    static {
        PermissionManager.get().addChecker(Fragment.class, new CompatFragmentPerChecker());
    }

    protected final ContainerListenerController mController = new ContainerListenerController();
    private ILoadingManager loadingManager;
    private Bundle mParams;
    private boolean isVisible;
    private IViewProcessor mViewProcessor;
    private final IBackPressInterceptor backPressInterceptor = new IBackPressInterceptor() {
        @Override
        public boolean onInterceptBackPress() {
            return mController.isInteractive() && (close() || mController.onInterceptBackPress());
        }
    };
    private final OnTouchKeyListener touchKeyListener = new OnTouchKeyListener() {

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            return mController.isInteractive() && mController.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            return mController.isInteractive() && mController.onKeyUp(keyCode, event);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CentralManager.onObjectInit(this);
        View theView = getView();
        if (mViewProcessor == null) {
            mViewProcessor = initViewProcessor();
            if (mViewProcessor != null) {
                mController.add(mViewProcessor, null);
                theView = mViewProcessor.getView();
            } else {
                Logger.w("IViewProcessor is null!");
            }
        }
        IViewContainer parentContainer = getParentContainer();
        if (parentContainer != null) {
            parentContainer.add(backPressInterceptor, IBackPressInterceptor.class);
            parentContainer.add(touchKeyListener, OnTouchKeyListener.class);
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
        if (mController.getStateCode() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
            IViewContainer parentContainer = getParentContainer();
            if (parentContainer != null) {
                parentContainer.remove(backPressInterceptor, IBackPressInterceptor.class);
                parentContainer.remove(touchKeyListener, OnTouchKeyListener.class);
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
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public Activity getCurrentActivity() {
        return getActivity();
    }

    @Override
    public ICustomWindowManager getCustomWindowManager() {
        return mController.getCustomWindowManager();
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        this.startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, @NonNull final OnActivityResultListener listener) {
        mController.addDisposable(listener, OnActivityResultListener.class);
        this.startActivityForResult(intent, intent.getIntExtra(AppConst.REQUEST_CODE, AppConst.DEF_REQUEST_CODE), null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        Activity activity = getActivity();
        if (!(activity instanceof IViewContainer)) {
            StackAnimUtil.onPush(getActivity(), intent.getIntExtra(AppConst.ANIM_TYPE, 0));
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
        showLoading(getString(R.string.support_request_loading));
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
            return ContainerType.CONTAINER_SINGLE_COMPAT_FRAGMENT;
        }
        return ContainerType.CONTAINER_COMPAT_FRAGMENT;
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
        androidx.fragment.app.Fragment parentFragment = getParentFragment();
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
    public IViewState getState() {
        return mController;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        mController.onParamChanged(newParams);
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        mController.onVisibleChanged(visible);
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
        }
    }

    protected IViewProcessor initViewProcessor() {
        return ContainerController.createViewProcessor(this, getParams(), null);
    }

    protected void initView(@Nullable Bundle savedInstanceState) {

    }

}