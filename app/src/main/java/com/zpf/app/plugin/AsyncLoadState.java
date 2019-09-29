package com.zpf.app.plugin;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef(value = {AsyncLoadState.RESULT_SUCCESS, AsyncLoadState.RESULT_NULL, AsyncLoadState.CONFIG_NULL,
        AsyncLoadState.CONFIG_ERROR, AsyncLoadState.UPDATE_FORCE, AsyncLoadState.UPDATE_NEED,
        AsyncLoadState.UPDATE_LOSS, AsyncLoadState.PARAM_ERROR, AsyncLoadState.LOADING
})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncLoadState {
    int RESULT_SUCCESS = 0;
    int RESULT_NULL = 1;
    int CONFIG_NULL = 2;
    int CONFIG_ERROR = 3;
    int UPDATE_FORCE = 4;
    int UPDATE_NEED = 5;
    int UPDATE_LOSS = 6;
    int PARAM_ERROR = 7;
    int LOADING = 8;
}