package com.zpf.support.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.support.constant.AppConst;
import com.zpf.support.constant.BaseKeyConst;
import com.zpf.support.defview.ProgressDialog;
import com.zpf.support.defview.RootLayout;
import com.zpf.support.util.CacheMap;
import com.zpf.support.util.ContainerListenerController;
import com.zpf.support.util.LifecycleLogUtil;
import com.zpf.support.util.PermissionUtil;
import com.zpf.support.util.PublicUtil;
import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.LifecycleInterface;
import com.zpf.support.interfaces.OnDestroyListener;
import com.zpf.support.interfaces.ResultCallBackListener;
import com.zpf.support.interfaces.RootLayoutInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.interfaces.ViewContainerInterface;
import com.zpf.support.interfaces.ViewInterface;
import com.zpf.support.interfaces.constant.LifecycleState;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by ZPF on 2018/6/14.
 */
public abstract class BaseFragment<T extends ViewInterface> extends Fragment implements ViewContainerInterface {
    protected T mView;
    private RootLayoutInterface mRootLayout;
    private boolean isVisible;
    private ProgressDialog loadingDialog;
    private final ContainerListenerController mController = new ContainerListenerController();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootLayout = createRootLayout();
        mRootLayout.getStatusBar().setVisibility(View.GONE);
        mRootLayout.getTitleBar().getLayout().setVisibility(View.GONE);
        View layoutView = getLayoutView();
        if (layoutView == null) {
            mRootLayout.setContentView(inflater, getLayoutId());
        } else {
            mRootLayout.setContentView(layoutView);
        }
        mView = createContent();
        mController.addLifecycleListener(mView);
        mController.addResultCallBackListener(mView);
        mController.onPreCreate(savedInstanceState);
        initView(savedInstanceState);
        mController.afterCreate(savedInstanceState);
        return mRootLayout.getLayout();
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
            loadingDialog = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mController.getState() < LifecycleState.AFTER_DESTROY) {
            mController.onDestroy();
            loadingDialog = null;
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

    @Override
    @LifecycleState
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
    public CallBackManagerInterface getCallBackManager() {
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
        showLoading(AppConst.PROGRESS_WAITTING);
    }

    @Override
    public void showLoading(String message) {
        if (getState() < LifecycleState.AFTER_DESTROY) {
            if (loadingDialog != null) {
                loadingDialog = getProgressDialog();
            }
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setText(message);
                loadingDialog.show();
            }
        }
    }

    protected RootLayoutInterface createRootLayout() {
        return new RootLayout(getContext());
    }

    protected T createContent() {
        Class<T> cls = PublicUtil.getViewClass(getClass());
        if (cls != null) {
            try {
                Class[] pType = new Class[]{ViewContainerInterface.class};
                Constructor<T> constructor = cls.getConstructor(pType);
                return constructor.newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected ProgressDialog getProgressDialog() {
        if (loadingDialog == null && getState() < LifecycleState.AFTER_DESTROY && getContext() != null) {
            loadingDialog = new ProgressDialog(getContext());
        }
        return loadingDialog;
    }

    public View getLayoutView() {
        return null;
    }

    public abstract int getLayoutId();

    public abstract void initView(@Nullable Bundle savedInstanceState);

    private void checkVisibleChange() {
        boolean newVisible = getUserVisibleHint() && isVisible();
        if (newVisible != this.isVisible) {
            this.isVisible = newVisible;
            mController.onVisibleChanged(newVisible);
        }
    }
}
