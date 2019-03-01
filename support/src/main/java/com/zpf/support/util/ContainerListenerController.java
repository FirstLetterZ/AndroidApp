package com.zpf.support.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.api.ICustomWindow;
import com.zpf.api.LifecycleListener;
import com.zpf.frame.ILifecycleMonitor;
import com.zpf.frame.ResultCallBackListener;
import com.zpf.tool.config.LifecycleState;
import com.zpf.tool.expand.util.CallBackManager;
import com.zpf.tool.expand.util.DialogController;
import com.zpf.tool.expand.util.ViewStateListener;
import com.zpf.tool.permission.ActivityPermissionChecker;
import com.zpf.tool.permission.CompatFragmentPermissionChecker;
import com.zpf.tool.permission.FragmentPermissionChecker;
import com.zpf.tool.permission.PermissionChecker;
import com.zpf.api.OnDestroyListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/6/28.
 */
public class ContainerListenerController implements ILifecycleMonitor, LifecycleListener, ResultCallBackListener {
    private final List<OnDestroyListener> mDestroyListenerList = new ArrayList<>();
    private final List<LifecycleListener> mLifecycleList = new ArrayList<>();
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
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onPreCreate(savedInstanceState);
        }
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.afterCreate(savedInstanceState);
        }
    }

    @Override
    public void onRestart() {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onRestart();
        }
    }

    @Override
    public void onStart() {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onStart();
        }
    }

    @Override
    public void onResume() {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onResume();
        }
    }

    @Override
    public void onPause() {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onPause();
        }
    }

    @Override
    public void onStop() {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        for (LifecycleListener lifecycle : mLifecycleList) {
            lifecycle.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        for (LifecycleListener lifecycle : mLifecycleList) {
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
    public void addLifecycleListener(LifecycleListener lifecycleListener) {
        if (mLifecycleList.size() == 0 || !mLifecycleList.contains(lifecycleListener)) {
            mLifecycleList.add(lifecycleListener);
        }
    }

    @Override
    public void removeLifecycleListener(LifecycleListener lifecycleListener) {
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

    public CompatFragmentPermissionChecker getSupportFragmentPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new CompatFragmentPermissionChecker();
        } else if (!(mPermissionChecker instanceof CompatFragmentPermissionChecker)) {
            mPermissionChecker.onDestroy();
            mPermissionChecker = new CompatFragmentPermissionChecker();
        }
        return (CompatFragmentPermissionChecker) mPermissionChecker;
    }
}
