package com.zpf.refresh.util;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2018/6/15.
 */
@IntDef(value = {RefreshLayoutState.INIT, RefreshLayoutState.TO_REFRESH,
        RefreshLayoutState.REFRESHING, RefreshLayoutState.TO_LOAD,
        RefreshLayoutState.LOADING, RefreshLayoutState.DONE,
        RefreshLayoutState.SUCCEED, RefreshLayoutState.FAIL,})
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshLayoutState {
    int INIT = 0;  // 初始状态
    int TO_REFRESH = 1; // 释放刷新
    int REFRESHING = 2;  // 正在刷新
    int TO_LOAD = 3;// 释放加载
    int LOADING = 4;// 正在加载
    int DONE = 5;// 操作完毕
    int SUCCEED = 10; // 刷新成功
    int FAIL = 11; // 刷新失败
}
