package com.zpf.baselib.cache;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by ZPF on 2018/6/13.
 */
@IntDef(value = {LifecycleState.BEFORE_CREATE, LifecycleState.AFTER_CREATE,
        LifecycleState.AFTER_START, LifecycleState.AFTER_RESUME, LifecycleState.AFTER_PAUSE,
        LifecycleState.AFTER_STOP, LifecycleState.AFTER_DESTROY,})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LifecycleState {
    int BEFORE_CREATE = 0;
    int AFTER_CREATE = 1;
    int AFTER_START = 2;
    int AFTER_RESUME = 3;
    int AFTER_PAUSE = 4;
    int AFTER_STOP = 5;
    int AFTER_DESTROY = 6;
}
