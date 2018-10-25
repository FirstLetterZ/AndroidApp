package com.zpf.support.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.support.api.LifecycleInterface;
import com.zpf.support.api.constant.LifecycleState;

/**
 * Created by ZPF on 2018/6/28.
 */

public class ViewStateListener implements LifecycleInterface {
    private int mViewState = -1;

    @Override
    public void onPreCreate(@Nullable Bundle savedInstanceState) {
        mViewState = LifecycleState.BEFORE_CREATE;
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        mViewState = LifecycleState.AFTER_CREATE;
    }

    @Override
    public void onStart() {
        mViewState = LifecycleState.AFTER_START;
    }

    @Override
    public void onResume() {
        mViewState = LifecycleState.AFTER_RESUME;
    }

    @Override
    public void onPause() {
        mViewState = LifecycleState.AFTER_PAUSE;
    }

    @Override
    public void onStop() {
        mViewState = LifecycleState.AFTER_STOP;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onDestroy() {
        mViewState = LifecycleState.AFTER_DESTROY;
    }

    @LifecycleState
    public int getState() {
        return mViewState;
    }
}
