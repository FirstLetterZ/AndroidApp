package com.zpf.network;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/6/24.
 */
public interface IResponse<T> {

    String requestId();

    boolean success();

    int code();

    @Nullable
    String message();

    @Nullable
    T data();
}