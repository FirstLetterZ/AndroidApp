package com.zpf.tool.network.base;

/**
 * @author Created by ZPF on 2021/2/26.
 */
public interface OnResponseListener<T> {
    void onResponse(boolean success, int code, T data, String msg);
}