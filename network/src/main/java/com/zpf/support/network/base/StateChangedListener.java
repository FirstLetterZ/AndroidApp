package com.zpf.support.network.base;

public interface StateChangedListener<T> {
    void onStateChanged(boolean loading,int code,String msg,T data);
}
