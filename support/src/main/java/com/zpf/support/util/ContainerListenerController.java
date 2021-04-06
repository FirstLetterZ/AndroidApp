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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/6/28.
 */
public class ContainerListenerController implements ILifecycleMonitor, IFullLifecycle, OnActivityResultListener,
        OnPermissionResultListener, OnTouchKeyListener, IBackPressInterceptor, IViewStateListener {
    private List<OnDestroyListener> mDestroyListenerList;
    private List<OnActivityResultListener> mActivityResultCallBackList;
    private List<OnPermissionResultListener> mPermissionCallBackList;
    private List<IBackPressInterceptor> mBackPressInterceptor;
    private List<OnTouchKeyListener> mTouchKeyListener;
    private List<IViewStateListener> mViewStateList;
    private DialogController mDialogController;
    private CancelableManager mCancelableManager;
    private final List<IFullLifecycle> mLifecycleList = new LinkedList<>();
    private final OnLifecycleStateListener mStateListener = new OnLifecycleStateListener();
    private PermissionChecker<?> mPermissionChecker;
    private boolean visible = false;

    public ContainerListenerController() {
        mLifecycleList.add(mStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mActivityResultCallBackList != null) {
            for (OnActivityResultListener listener : mActivityResultCallBackList) {
                listener.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionChecker != null) {
            mPermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (mPermissionCallBackList != null) {
            for (OnPermissionResultListener listener : mPermissionCallBackList) {
                listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public boolean onInterceptBackPress() {
        boolean result = false;
        if (mBackPressInterceptor != null) {
            for (IBackPressInterceptor listener : mBackPressInterceptor) {
                if (listener.onInterceptBackPress()) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = false;
        if (mTouchKeyListener != null) {
            for (OnTouchKeyListener listener : mTouchKeyListener) {
                if (listener.onKeyDown(keyCode, event)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result = false;
        if (mTouchKeyListener != null) {
            for (OnTouchKeyListener listener : mTouchKeyListener) {
                if (listener.onKeyUp(keyCode, event)) {
                    result = true;
                    break;
                }
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
        if (mCancelableManager != null) {
            mCancelableManager.onDestroy();
        }
        if (mDialogController != null) {
            mDialogController.onDestroy();
        }
        if (mDestroyListenerList != null) {
            for (OnDestroyListener listener : mDestroyListenerList) {
                listener.onDestroy();
            }
            mDestroyListenerList.clear();
        }
        for (IFullLifecycle lifecycle : mLifecycleList) {
            lifecycle.onDestroy();
        }
        mLifecycleList.clear();
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
        if (mDialogController == null) {
            mDialogController = new DialogController();
        }
        window.toBind(mDialogController);
    }


    @Override
    public boolean close() {
        if (mDialogController == null) {
            return false;
        }
        return mDialogController.execute(-1);
    }

    @Override
    public synchronized CancelableManager getCancelableManager() {
        if (mCancelableManager == null) {
            mCancelableManager = new CancelableManager();
        }
        return mCancelableManager;
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
                if (mDestroyListenerList == null) {
                    mDestroyListenerList = new LinkedList<>();
                }
                if (mDestroyListenerList.size() == 0 || !mDestroyListenerList.contains(listener)) {
                    mDestroyListenerList.add((OnDestroyListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == OnActivityResultListener.class) && listener instanceof OnActivityResultListener) {
                if (mActivityResultCallBackList == null) {
                    mActivityResultCallBackList = new LinkedList<>();
                }
                if (mActivityResultCallBackList.size() == 0 || !mActivityResultCallBackList.contains(listener)) {
                    mActivityResultCallBackList.add((OnActivityResultListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == OnPermissionResultListener.class) && listener instanceof OnPermissionResultListener) {
                if (mPermissionCallBackList == null) {
                    mPermissionCallBackList = new LinkedList<>();
                }
                if (mPermissionCallBackList.size() == 0 || !mPermissionCallBackList.contains(listener)) {
                    mPermissionCallBackList.add((OnPermissionResultListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == IBackPressInterceptor.class) && listener instanceof IBackPressInterceptor) {
                if (mBackPressInterceptor == null) {
                    mBackPressInterceptor = new LinkedList<>();
                }
                if (mBackPressInterceptor.size() == 0 || !mBackPressInterceptor.contains(listener)) {
                    mBackPressInterceptor.add((IBackPressInterceptor) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == OnTouchKeyListener.class) && listener instanceof OnTouchKeyListener) {
                if (mTouchKeyListener == null) {
                    mTouchKeyListener = new LinkedList<>();
                }
                if (mTouchKeyListener.size() == 0 || !mTouchKeyListener.contains(listener)) {
                    mTouchKeyListener.add((OnTouchKeyListener) listener);
                }
                result = true;
            }
            if ((listenerClass == null || listenerClass == IViewStateListener.class) && listener instanceof IViewStateListener) {
                if (mViewStateList == null) {
                    mViewStateList = new LinkedList<>();
                }
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
                result = mDestroyListenerList != null && mDestroyListenerList.remove(listener);
            }
            if ((listenerClass == null || listenerClass == OnActivityResultListener.class) && listener instanceof OnActivityResultListener) {
                result = (mActivityResultCallBackList != null && mActivityResultCallBackList.remove(listener)) || result;
            }
            if ((listenerClass == null || listenerClass == OnPermissionResultListener.class) && listener instanceof OnPermissionResultListener) {
                result = (mPermissionCallBackList != null && mPermissionCallBackList.remove(listener)) || result;
            }
            if ((listenerClass == null || listenerClass == IBackPressInterceptor.class) && listener instanceof IBackPressInterceptor) {
                result = (mBackPressInterceptor != null && mBackPressInterceptor.remove(listener)) || result;
            }
            if ((listenerClass == null || listenerClass == OnTouchKeyListener.class) && listener instanceof OnTouchKeyListener) {
                result = (mTouchKeyListener != null && mTouchKeyListener.remove(listener)) || result;
            }
            if ((listenerClass == null || listenerClass == IViewStateListener.class) && listener instanceof IViewStateListener) {
                result = (mViewStateList != null && mViewStateList.remove(listener)) || result;
            }
        }
        return result;
    }

    @Override
    public void onParamChanged(Bundle newParams) {
        if (mViewStateList != null) {
            for (IViewStateListener listener : mViewStateList) {
                listener.onParamChanged(newParams);
            }
        }
    }

    @Override
    public void onVisibleChanged(boolean visible) {
        this.visible = visible;
        if (mViewStateList != null) {
            for (IViewStateListener listener : mViewStateList) {
                listener.onVisibleChanged(visible);
            }
        }
    }

    @Override
    public void onActivityChanged(boolean activity) {
        if (mViewStateList != null) {
            for (IViewStateListener listener : mViewStateList) {
                listener.onActivityChanged(activity);
            }
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
