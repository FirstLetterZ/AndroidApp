package com.zpf.support.interfaces;

/**
 * 缺少权限
 * Created by ZPF on 2018/8/22.
 */
public interface OnLackOfPermissions {
    void onLack(String[] permissions);
}