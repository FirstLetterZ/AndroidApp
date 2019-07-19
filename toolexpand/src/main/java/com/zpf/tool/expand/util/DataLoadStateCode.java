package com.zpf.tool.expand.util;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef(value = {
        DataLoadStateCode.UNDEFINED, DataLoadStateCode.LOADING, DataLoadStateCode.EMPTY,
        DataLoadStateCode.ERROR, DataLoadStateCode.SUCCESS,
})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataLoadStateCode {
    int UNDEFINED = 0;
    int LOADING = 1;
    int EMPTY = 2;
    int ERROR = 3;
    int SUCCESS = 4;
}
