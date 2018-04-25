package com.zpf.appLib.base;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Window;

import java.util.List;

import io.reactivex.Observer;

/**
 * 替代activity与fragment
 * Created by ZPF on 2018/3/22.
 */
public interface BaseViewContainer {
    //是否已创建
    boolean isCreated();

    //是否已销毁
    boolean isDestroy();

    //是否为activity
    boolean isActivity();

    //获取上下文
    Context getContext();

    Window getWindow();

    //获取当前默认的LayoutInflater
    LayoutInflater getViewInflater();

    Intent obtainIntent(@Nullable Intent intent);

    void pickActivity(Class<? extends BaseViewLayout> viewClass);

    void pushActivity(Class<? extends BaseViewLayout> viewClass);

    void pickActivity(String name);

    void pushActivity(String name);

    void pushActivityForResult(Class<? extends BaseViewLayout> viewClass, int requestCode);

    void pushActivityForResult(String name, int requestCode);

    void pushActivityArray(List<Class<? extends BaseViewLayout>> viewClass);

    //绑定生命周期的弹窗
    void showDialog(BaseDialog dialog);

    //绑定生命周期的网络请求
    void addRequest(Observer observer);

    //activity与fragment预留交互通道
    void sendMessage(String name, Object value);

    void handleMessage(String name, Object value);

    void finishWithResult(int resultCode, Intent data);

    void finish();

    void exit();
}
