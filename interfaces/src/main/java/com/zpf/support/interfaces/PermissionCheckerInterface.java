package com.zpf.support.interfaces;

/**
 * 权限申请
 * Created by ZPF on 2018/8/22.
 */
public interface PermissionCheckerInterface {
    boolean checkPermissions(String... permissions);

    boolean checkPermissions(int requestCode, String... permissions);

    void checkPermissions(Runnable runnable, OnLackOfPermissions onLackOfPermissions, String... permissions);

    void checkPermissions(Runnable runnable, OnLackOfPermissions onLackOfPermissions, int requestCode, String... permissions);

}
