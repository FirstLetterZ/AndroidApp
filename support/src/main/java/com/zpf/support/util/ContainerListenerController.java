package com.zpf.support.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IFullLifecycle;
import com.zpf.api.OnActivityResultListener;
import com.zpf.api.OnPermissionResultListener;
import com.zpf.api.OnTouchKeyListener;
import com.zpf.frame.ILifecycleMonitor;
import com.zpf.frame.IViewStateListener;
import com.zpf.tool.expand.util.CancelableManager;
import com.zpf.tool.permission.ActivityPermissionChecker;
import com.zpf.tool.compat.permission.CompatPermissionChecker;
import com.zpf.tool.permission.FragmentPermissionChecker;
import com.zpf.tool.permission.PermissionChecker;
import com.zpf.tool.expand.util.DialogController;
import com.zpf.api.OnDestroyListener;
import com.zpf.tool.stack.LifecycleState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/6/28.
 */
public class ContainerListenerController implements ILifecycleMonitor, IFullLifecycle, OnActivityResultListener,
        OnPermissionResultListener, OnTouchKeyListener, IBackPressInterceptor, IViewStateListener {
    private final List<OnDestroyListener> mDestroyListenerList = new ArrayList<>();
    private final List<IFullLifecycle> mLifecycleList = new ArrayList<>();
    private final List<OnActivityResultListener> mActivityResultCallBackList = new ArrayList<>();
    private final List<OnPermissionResultListener> mPermissionCallBackList = new ArrayList<>();
    private final List<IBackPressInterceptor> mBackPressInterceptor = new ArrayList<>();
    private final List<OnTouchKeyListener> mTouchKeyListener = new ArrayList<>();
    private final List<IViewStateListener> mViewStateList = new ArrayList<>();
    private final DialogController mDialogController = new DialogController();
    private final CancelableManager mCallBackManager = new CancelableManager();
    private final OnLifecycleStateListener mStateListener = new OnLifecycleStateListener();
    private PermissionChecker<?> mPermissionChecker;
    private boolean visible = false;

    public ContainerListenerController() {
        mLifecycleList.add(mStateListener);
        mDestroyListenerList.add(mCallBackManager);
        mDestroyListenerList.add(mDialogController);
    }

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = false;
        for (OnTouchKeyListener listener : mTouchKeyListener) {
            if (listener.onKeyDown(keyCode, event)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result = false;
        for (OnTouchKeyListener listener : mTouchKeyListener) {
            if (listener.onKeyUp(keyCode, event)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onCreate(savedInstanceState);
        }
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
        if (mPermissionChecker != null) {
            mPermissionChecker = null;
        }
    }

    @Override
    @LifecycleState
    public int getState() {
        return mStateListener.getState();
    }

    @Override
    public boolean living() {
        return mStateListener.getState() >= LifecycleState.BEFORE_CREATE
                && mStateListener.getState() < LifecycleState.AFTER_DESTROY;
    }

    @Override
    public boolean interactive() {
        return mStateListener.getState() == LifecycleState.AFTER_RESUME;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void show(ICustomWindow window) {
        window.toBind(mDialogController);
    }


    @Override
    public boolean close() {
        return mDialogController.execute(-1);
    }

    @Override
    public CancelableManager getCancelableManager() {
        return mCallBackManager;
    }

    @Override
    public boolean addListener(Object listener, @Nullable Type listenerClass) {
        boolean result = false;
        if (listener != null) {
            if ((listenerClass == null || listenerClass == IFullLifecycle.class) && listener instanceof IFullLifecycle) {
                if (mLifecycleList.size() == 0 || !mLifecycleList.contains(listener)) {
                    mLifecycleList.add((IFullLifecycle) listener);
                }
                result = true;
            } else if ((listenerClass == null || listenerClass == OnDestroyListener.class) && listener instanceof OnDestroyListener) {
                if (mDestroyListenerList.size() == 0 || !mDestroyListenerList.contains(listener)) {
                    mDestroyListenerList.add((OnDestroyListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == OnActivityResultListener.class) && listener instanceof OnActivityResultListener) {
                if (mActivityResultCallBackList.size() == 0 || !mActivityResultCallBackList.contains(listener)) {
                    mActivityResultCallBackList.add((OnActivityResultListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == OnPermissionResultListener.class) && listener instanceof OnPermissionResultListener) {
                if (mPermissionCallBackList.size() == 0 || !mPermissionCallBackList.contains(listener)) {
                    mPermissionCallBackList.add((OnPermissionResultListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == IBackPressInterceptor.class) && listener instanceof IBackPressInterceptor) {
                if (mBackPressInterceptor.size() == 0 || !mBackPressInterceptor.contains(listener)) {
                    mBackPressInterceptor.add((IBackPressInterceptor) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == OnTouchKeyListener.class) && listener instanceof OnTouchKeyListener) {
                if (mTouchKeyListener.size() == 0 || !mTouchKeyListener.contains(listener)) {
                    mTouchKeyListener.add((OnTouchKeyListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == IViewStateListener.class) && listener instanceof IViewStateListener) {
                if (mViewStateList.size() == 0 || !mViewStateList.contains(listener)) {
                    mViewStateList.add((IViewStateListener) listener);
                }
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean removeListener(Object listener, @Nullable Type listenerClass) {
        boolean result = false;
        if (listener != null) {
            if ((listenerClass == null || listenerClass == IFullLifecycle.class) && listener instanceof IFullLifecycle) {
                result = mLifecycleList.remove(listener);
            } else if ((listenerClass == null || listenerClass == OnDestroyListener.class) && listener instanceof OnDestroyListener) {
                result = mDestroyListenerList.remove(listener);
            }
            if ((listenerClass == null || listenerClass == OnActivityResultListener.class) && listener instanceof OnActivityResultListener) {
                result = mActivityResultCallBackList.remove(listener) || result;
            }
            if ((listenerClass == null || listenerClass == OnPermissionResultListener.class) && listener instanceof OnPermissionResultListener) {
                result = mPermissionCallBackList.remove(listener) || result;
            }
            if ((listenerClass == null || listenerClass == IBackPressInterceptor.class) && listener instanceof IBackPressInterceptor) {
                result = mBackPressInterceptor.remove(listener) || result;
            }
            if ((listenerClass == null || listenerClass == IViewStateListener.class) && listener instanceof IViewStateListener) {
                result = mViewStateList.remove(listener) || result;
            }
        }
        return result;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        for (IViewStateListener listener : mViewStateList) {
            listener.onParamChanged(newParams);
        }

    }

    @Override
    public void onVisibleChanged(boolean visible) {
        this.visible = visible;
        for (IViewStateListener listener : mViewStateList) {
            listener.onVisibleChanged(visible);
        }
    }

    @Override
    public void onActivityChanged(boolean activity) {
        for (IViewStateListener listener : mViewStateList) {
            listener.onActivityChanged(activity);
        }
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
            mPermissionChecker = new FragmentPermissionChecker();
        }
        return (FragmentPermissionChecker) mPermissionChecker;
    }

    public CompatPermissionChecker getSupportFragmentPermissionChecker() {
        if (mPermissionChecker == null) {
            mPermissionChecker = new CompatPermissionChecker();
        } else if (!(mPermissionChecker instanceof CompatPermissionChecker)) {
            mPermissionChecker = new CompatPermissionChecker();
        }
        return (CompatPermissionChecker) mPermissionChecker;
    }

}
