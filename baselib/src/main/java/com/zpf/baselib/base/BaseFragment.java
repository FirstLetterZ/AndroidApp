package com.zpf.baselib.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.baselib.cache.LifecycleState;
import com.zpf.baselib.interfaces.LifecycleInterface;
import com.zpf.baselib.interfaces.OnDestroyListener;
import com.zpf.baselib.interfaces.ResultCallBackListener;
import com.zpf.baselib.interfaces.RootLayoutInterface;
import com.zpf.baselib.interfaces.SafeWindowInterface;
import com.zpf.baselib.interfaces.ViewContainerInterface;
import com.zpf.baselib.interfaces.ViewInterface;
import com.zpf.baselib.util.CallBackManager;
import com.zpf.baselib.util.LifecycleLogUtil;
import com.zpf.baselib.util.PublicUtil;

import java.lang.reflect.Constructor;

/**
 * Created by ZPF on 2018/6/14.
 */
public abstract class BaseFragment<T extends ViewInterface> extends Fragment implements ViewContainerInterface {
    protected T mView;
    private RootLayout mRootLayout;
    private boolean isVisible;
    private final ContainerListenerController mController = new ContainerListenerController();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (PublicUtil.isDebug()) {
            new LifecycleLogUtil(this);
        }
        mRootLayout = new RootLayout(getContext());
        if (getLayoutView() == null) {
            mRootLayout.setContentView(inflater, getLayoutId());
        } else {
            mRootLayout.setContentView(getLayoutView());
        }
        mRootLayout.getStatusBar().setVisibility(View.GONE);
        mRootLayout.getTitleBar().getLayout().setVisibility(View.GONE);
        try {
            Class<T> cls = PublicUtil.getViewClass(getClass());
            if (cls != null) {
                Class[] pType = new Class[]{ViewContainerInterface.class};
                Constructor<T> constructor = cls.getConstructor(pType);
                mView = constructor.newInstance(this);
                mController.addLifecycleListener(mView);
                mController.addResultCallBackListener(mView);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        mController.onPreCreate(savedInstanceState);
        initView(savedInstanceState);
        mController.afterCreate(savedInstanceState);
        return mRootLayout;
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
        mController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    public int getState() {
        return mController.getState();
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
    public RootLayoutInterface getRootLayout() {
        return mRootLayout;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
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
        boolean result = false;
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            result = ((BaseActivity) activity).hideLoading();
        }
        return result;
    }

    @Override
    public void showLoading() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).showLoading();
        }
    }

    @Override
    public void showLoading(String message) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).showLoading(message);
        }
    }

    public View getLayoutView() {
        return null;
    }

    public abstract int getLayoutId();

    public abstract void initView(@Nullable Bundle savedInstanceState);

    private void checkVisibleChange() {
        boolean newVisible = isVisible();
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
        }
    }
}
