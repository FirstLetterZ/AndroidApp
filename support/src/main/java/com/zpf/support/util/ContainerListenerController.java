package com.zpf.support.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.IPermissionChecker;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.IViewLifecycle;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.frame.ILifecycleMonitor;
import com.zpf.frame.ResultCallBackListener;
import com.zpf.tool.permission.ActivityPermissionChecker;
import com.zpf.tool.compat.permission.CompatPermissionChecker;
import com.zpf.tool.permission.FragmentPermissionChecker;
import com.zpf.tool.permission.PermissionChecker;
import com.zpf.tool.config.LifecycleState;
import com.zpf.tool.expand.util.CallBackManager;
import com.zpf.tool.expand.util.DialogController;
import com.zpf.tool.expand.util.ViewStateListener;
import com.zpf.api.OnDestroyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/6/28.
 */
public class ContainerListenerController implements ILifecycleMonitor, IFullLifecycle, OnActivityResultListener,
        OnPermissionResultListener, IBackPressInterceptor ,IViewLifecycle {
    private final List<OnDestroyListener> mDestroyListenerList = new ArrayList<>();
    private final List<IFullLifecycle> mLifecycleList = new ArrayList<>();
    private final List<OnActivityResultListener> mActivityResultCallBackList = new ArrayList<>();
    private final List<OnPermissionResultListener> mPermissionCallBackList = new ArrayList<>();
    private final List<IBackPressInterceptor> mBackPressInterceptor = new ArrayList<>();
    private final DialogController mDialogController = new DialogController();
    private final CallBackManager mCallBackManager = new CallBackManager();
    private final ViewStateListener mStateListener = new ViewStateListener();
    private PermissionChecker mPermissionChecker;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (OnActivityResultListener listener : mActivityResultCallBackList) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionChecker != null) {
            mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        for (OnPermissionResultListener listener : mPermissionCallBackList) {
            listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onVisibleChanged(boolean visibility) {
        for (ResultCallBackListener listener : mCallBackList) {
            listener.onVisibleChanged(visibility);
        }
    }

    @Override
    public void onActivityChanged(boolean isActivity) {

    }

    @Override
    public boolean onInterceptBackPress() {
        boolean result = false;
        for (IBackPressInterceptor listener : mBackPressInterceptor) {
            if (listener.onInterceptBackPress()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mLifecycleList.add(mStateListener);
        mDestroyListenerList.add(mCallBackManager);
        mDestroyListenerList.add(mDialogController);
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onPreCreate(savedInstanceState);
        }
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.afterCreate(savedInstanceState);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onRestart() {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onRestart();
        }
    }

    @Override
    public void onStart() {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onStart();
        }
    }

    @Override
    public void onResume() {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onResume();
        }
    }

    @Override
    public void onPause() {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onPause();
        }
    }

    @Override
    public void onStop() {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onDestroy();
        }
        for (OnDestroyListener listener : mDestroyListenerList) {
            listener.onDestroy();
        }
        mLifecycleList.clear();
        mDestroyListenerList.clear();
        mCallBackList.clear();
        if (mPermissionChecker != null) {
            mPermissionChecker.onDestroy();
            mPermissionChecker = null;
        }
    }

    @Override
    @LifecycleState
    public int getState() {
        return mStateListener.getState();
    }

    @Override
    public boolean isLiving() {
        return mStateListener.getState() >= LifecycleState.BEFORE_CREATE
                && mStateListener.getState() < LifecycleState.AFTER_DESTROY;
    }

    @Override
    public boolean isActive() {
        return mStateListener.getState() == LifecycleState.AFTER_RESUME;
    }

    @Override
    public void show(ICustomWindow window) {
        mDialogController.show(window);
    }


    @Override
    public boolean dismiss() {
        return mDialogController.execute(-1);
    }

    @Override
    public CallBackManager getCallBackManager() {
        return mCallBackManager;
    }

    @Override
    public void addLifecycleListener(IFullLifecycle lifecycleListener) {

    }

    @Override
    public void removeLifecycleListener(IFullLifecycle lifecycleListener) {

    }

    @Override
    public void addIFullLifecycle(IFullLifecycle IFullLifecycle) {
        if (mLifecycleList.size() == 0 || !mLifecycleList.contains(IFullLifecycle)) {
            mLifecycleList.add(IFullLifecycle);
        }
    }

    @Override
    public void removeIFullLifecycle(IFullLifecycle IFullLifecycle) {
        mLifecycleList.remove(IFullLifecycle);
    }

    @Override
    public void addOnDestroyListener(OnDestroyListener listener) {
        if (mDestroyListenerList.size() == 0 || !mDestroyListenerList.contains(listener)) {
            mDestroyListenerList.add(listener);
        }
    }

    @Override
    public void removeOnDestroyListener(OnDestroyListener listener) {
        mDestroyListenerList.remove(listener);
    }

    @Override
    public void addActivityResultListener(OnActivityResultListener listener) {

    }

    @Override
    public void removeActivityResultListener(OnActivityResultListener listener) {

    }

    @Override
    public void addPermissionsResultListener(OnPermissionResultListener listener) {

    }

    @Override
    public void removePermissionsResultListener(OnPermissionResultListener listener) {

    }

    @Override
    public void addBackPressInterceptor(IBackPressInterceptor interceptor) {

    }

    @Override
    public void removeBackPressInterceptor(IBackPressInterceptor interceptor) {

    }

    @Override
    public void addPermissionsResultListener(IPermissionChecker listener) {

    }

    @Override
    public void removePermissionsResultListener(IPermissionChecker listener) {

    }

    @Override
    public void addResultCallBackListener(ResultCallBackListener callBackListener) {
        if (mCallBackList.size() == 0 || !mCallBackList.contains(callBackListener)) {
            mCallBackList.add(callBackListener);
        }
    }

    @Override
    public void removeResultCallBackListener(ResultCallBackListener callBackListener) {
        mCallBackList.remove(callBackListener);
    }

    public ActivityPermissionChecker getActivityPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new ActivityPermissionChecker();
        } else if (!(mPermissionChecker instanceof ActivityPermissionChecker)) {
            mPermissionChecker.onDestroy();
            mPermissionChecker = new ActivityPermissionChecker();
        }
        return (ActivityPermissionChecker) mPermissionChecker;
    }

    public FragmentPermissionChecker getFragmentPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new FragmentPermissionChecker();
        } else if (!(mPermissionChecker instanceof FragmentPermissionChecker)) {
            mPermissionChecker.onDestroy();
            mPermissionChecker = new FragmentPermissionChecker();
        }
        return (FragmentPermissionChecker) mPermissionChecker;
    }

    public CompatPermissionChecker getSupportFragmentPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new CompatPermissionChecker();
        } else if (!(mPermissionChecker instanceof CompatPermissionChecker)) {
            mPermissionChecker.onDestroy();
            mPermissionChecker = new CompatPermissionChecker();
        }
        return (CompatPermissionChecker) mPermissionChecker;
    }
}
