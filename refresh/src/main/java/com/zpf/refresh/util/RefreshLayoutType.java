package com.zpf.refresh.util;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2018/6/15.
 */
@IntDef(value = {RefreshLayoutType.NO_STRETCHY, RefreshLayoutType.BOTH_UP_DOWN,
        RefreshLayoutType.ONLY_PULL_DOWN, RefreshLayoutType.ONLY_PULL_UP,
        RefreshLayoutType.ONLY_STRETCHY})
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshLayoutType {
    int NO_STRETCHY = 0;//不可上下滑动
    int BOTH_UP_DOWN = 1;//可以刷拉或加载
    int ONLY_PULL_DOWN = 2;//仅下拉
    int ONLY_PULL_UP = 3;//仅上拉
    int ONLY_STRETCHY = 4;//可上下拉但不刷新不加载
}
