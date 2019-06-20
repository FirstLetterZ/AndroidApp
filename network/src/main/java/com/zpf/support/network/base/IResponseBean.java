package com.zpf.support.network.base;

public interface IResponseBean<T> {
    boolean isSuccess();

    String getMessage();

    int getCode();

    T getData();
}
