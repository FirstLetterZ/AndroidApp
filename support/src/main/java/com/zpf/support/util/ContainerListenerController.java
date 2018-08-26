package com.zpf.support.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.support.generalUtil.permission.ActivityPermissionChecker;
import com.zpf.support.generalUtil.permission.CompatFragmentPermissionChecker;
import com.zpf.support.generalUtil.permission.FragmentPermissionChecker;
import com.zpf.support.generalUtil.permission.PermissionChecker;
import com.zpf.support.interfaces.LifecycleInterface;
import com.zpf.support.interfaces.LifecycleListenerController;
import com.zpf.support.interfaces.OnDestroyListener;
import com.zpf.support.interfaces.ResultCallBackListener;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.interfaces.constant.LifecycleState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/6/28.
 */
public class ContainerListenerController implements LifecycleListenerController, LifecycleInterface, ResultCallBackListener {
    private final List<OnDestroyListener> mDestroyListenerList = new ArrayList<>();
    private final List<LifecycleInterface> mLifecycleList = new ArrayList<>();
    private final List<ResultCallBackListener> mCallBackList = new ArrayList<>();
    private final DialogController mDialogController = new DialogController();
    private final CallBackManager mCallBackManager = new CallBackManager();
    private final ViewStateListener mStateListener = new ViewStateListener();
    private PermissionChecker mPermissionChecker;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (ResultCallBackListener listener : mCallBackList) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionChecker != null) {
            mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        for (ResultCallBackListener listener : mCallBackList) {
            listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {
        for (ResultCallBackListener listener : mCallBackList) {
            listener.onNewIntent(intent);
        }
    }

    @Override
    public void onVisibleChanged(boolean visibility) {
        for (ResultCallBackListener listener : mCallBackList) {
            listener.onVisibleChanged(visibility);
        }
    }

    @Override
    public void onPreCreate(@Nullable Bundle savedInstanceState) {
        mLifecycleList.add(mStateListener);
        mDestroyListenerList.add(mCallBackManager);
        mDestroyListenerList.add(mDialogController);
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onPreCreate(savedInstanceState);
        }
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.afterCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onStart();
        }
    }

    @Override
    public void onResume() {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onResume();
        }
    }

    @Override
    public void onPause() {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onPause();
        }
    }

    @Override
    public void onStop() {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        for (LifecycleInterface lifecycle : mLifecycleList) {
            lifecycle.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        for (LifecycleInterface lifecycle : mLifecycleList) {
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
        return mStateListener.getState() >= LifecycleState.AFTER_CREATE
                && mStateListener.getState() < LifecycleState.AFTER_DESTROY;
    }

    @Override
    public boolean isActive() {
        return mStateListener.getState() == LifecycleState.AFTER_RESUME;
    }

    @Override
    public void show(SafeWindowInterface window) {
        mDialogController.show(window);
    }

    @Override
    public boolean dismiss() {
        return mDialogController.dismiss();
    }

    @Override
    public CallBackManager getCallBackManager() {
        return mCallBackManager;
    }

    @Override
    public void addLifecycleListener(LifecycleInterface lifecycleListener) {
        if (mLifecycleList.size() == 0 || !mLifecycleList.contains(lifecycleListener)) {
            mLifecycleList.add(lifecycleListener);
        }
    }

    @Override
    public void removeLifecycleListener(LifecycleInterface lifecycleListener) {
        mLifecycleList.remove(lifecycleListener);
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
            mPermissionChecker = new ActivityPermissionChecker();
        }
        return (ActivityPermissionChecker) mPermissionChecker;
    }

    public FragmentPermissionChecker getFragmentPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new FragmentPermissionChecker();
        } else if (!(mPermissionChecker instanceof FragmentPermissionChecker)) {
            mPermissionChecker.onDestroy();
        }
        return (FragmentPermissionChecker) mPermissionChecker;
    }

    public CompatFragmentPermissionChecker getSupportFragmentPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new CompatFragmentPermissionChecker();
        } else if (!(mPermissionChecker instanceof CompatFragmentPermissionChecker)) {
            mPermissionChecker = new CompatFragmentPermissionChecker();
        }
        return (CompatFragmentPermissionChecker) mPermissionChecker;
    }
}
