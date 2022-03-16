package com.zpf.views.bounce;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2018/6/15.
 */
@IntDef(value = {BounceLayoutState.INIT,
        BounceLayoutState.TO_REFRESH,
        BounceLayoutState.REFRESHING,
        BounceLayoutState.TO_LOAD,
        BounceLayoutState.LOADING,
        BounceLayoutState.SUCCEED,
        BounceLayoutState.FAIL
})
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BounceLayoutState {
    int INIT = 0;  // 初始状态
    int TO_REFRESH = 1; // 释放刷新
    int TO_LOAD = 2;// 释放加载
    int REFRESHING = 3;  // 正在刷新
    int LOADING = 4;// 正在加载
    int SUCCEED = 5; // 刷新成功
    int FAIL = 6; // 刷新失败
}
