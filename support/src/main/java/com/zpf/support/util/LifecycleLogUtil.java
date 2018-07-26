package com.zpf.support.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.support.interfaces.LifecycleInterface;
import com.zpf.support.interfaces.ViewContainerInterface;

/**
 * Created by ZPF on 2018/6/26.
 */
public class LifecycleLogUtil implements LifecycleInterface {
    private ViewContainerInterface viewContainerInterface;

    public LifecycleLogUtil(@NonNull ViewContainerInterface viewContainerInterface) {
        this.viewContainerInterface = viewContainerInterface;
        onLogUtilInit();
        viewContainerInterface.addLifecycleListener(this);
    }

    private void onLogUtilInit() {
        log("onLogUtilInit");
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        viewContainerInterface = null;
    }

    @Override
    public void onPreCreate(@Nullable Bundle savedInstanceState) {
        log("onPreCreate");
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        log("afterCreate");
    }

    @Override
    public void onStart() {
        log("onStart");
    }

    @Override
    public void onResume() {
        log("onResume");
    }

    @Override
    public void onPause() {
        log("onPause");
    }

    @Override
    public void onStop() {
        log("onStop");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        log("onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        log("onRestoreInstanceState");
    }

    private void log(String msg) {
        if (viewContainerInterface != null) {
            LogUtil.d(viewContainerInterface.getClass().getName() + " ===>>> " + msg);
        }
    }
}
