package com.zpf.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by ZPF on 2019/1/23.
 */

public interface CacheMapInterface {

    CacheMapInterface put(int key, Object value);

    @NonNull
    <T> T get(int key, @NonNull T defaultValue);

    @Nullable
    <T> T get(int key, @NonNull Class<T> cls);

    @Nullable
    <T> List<T> getList(int key, @NonNull Class<T> cls);

    CacheMapInterface clear();

    CacheMapInterface beginTransaction();

    boolean endTransaction();

}
