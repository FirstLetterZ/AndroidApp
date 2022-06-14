package com.zpf.support.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2019/5/15.
 */
@IntDef(value = {
        ContainerType.CONTAINER_ACTIVITY,
        ContainerType.CONTAINER_FRAGMENT,
        ContainerType.CONTAINER_COMPAT_ACTIVITY,
        ContainerType.CONTAINER_COMPAT_FRAGMENT,
        ContainerType.CONTAINER_SINGLE_ACTIVITY,
        ContainerType.CONTAINER_SINGLE_FRAGMENT,
        ContainerType.CONTAINER_SINGLE_COMPAT_ACTIVITY,
        ContainerType.CONTAINER_SINGLE_COMPAT_FRAGMENT,
        ContainerType.CONTAINER_CUSTOM,
        ContainerType.CONTAINER_COMPAT_CUSTOM,
        ContainerType.CONTAINER_DIALOG,
        ContainerType.CONTAINER_OTHER_CUSTOM
})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainerType {
    int CONTAINER_ACTIVITY = 1;
    int CONTAINER_FRAGMENT = 2;
    int CONTAINER_COMPAT_ACTIVITY = 3;
    int CONTAINER_COMPAT_FRAGMENT = 4;
    int CONTAINER_SINGLE_ACTIVITY = 5;
    int CONTAINER_SINGLE_FRAGMENT = 6;
    int CONTAINER_SINGLE_COMPAT_ACTIVITY = 7;
    int CONTAINER_SINGLE_COMPAT_FRAGMENT = 8;
    int CONTAINER_CUSTOM = 9;
    int CONTAINER_COMPAT_CUSTOM = 10;
    int CONTAINER_DIALOG = 11;
    int CONTAINER_OTHER_CUSTOM = 12;
}
