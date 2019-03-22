package com.zpf.support.util;


import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.zpf.tool.compat.permission.PermissionChecker;

/**
 * Created by ZPF on 2019/3/22.
 */

public class TestChecker extends PermissionChecker<Object> {

    @Override
    boolean checkEffective(Object target) {
        return target != null && target.getContext() != null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    boolean hasPermission(Object target, String p) {
        try {
            return target.getContext().checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    void requestPermissions(Fragment target, String[] p, int code) {
        target.requestPermissions(p, code);
    }


}
