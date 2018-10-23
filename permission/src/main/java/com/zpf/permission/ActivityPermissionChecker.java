package com.zpf.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用activity检查权限
 * Created by ZPF on 2018/8/22.
 */
public class ActivityPermissionChecker extends PermissionChecker {

    public boolean checkPermissions(@NonNull Activity activity, String... permissions) {
        return checkPermissions(activity, REQ_PERMISSION_CODE, permissions);
    }

    public boolean checkPermissions(@NonNull Activity activity, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> missPermissionList = new ArrayList<>();
            for (String per : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                }
            }
            if (missPermissionList.size() > 0) {
                int size = missPermissionList.size();
                ActivityCompat.requestPermissions(activity, missPermissionList.toArray(new String[size]), requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void checkPermissions(@NonNull Activity activity, Runnable runnable, Runnable onLackOfPermissions, String... permissions) {
        checkPermissions(activity, runnable, onLackOfPermissions, REQ_PERMISSION_CODE, permissions);
    }

    public void checkPermissions(@NonNull Activity activity, Runnable runnable, Runnable onLackOfPermissions, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> missPermissionList = new ArrayList<>();
            for (String per : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                }
            }
            if (missPermissionList.size() > 0) {
                permissionCallBack.put(requestCode, new Pair<>(runnable, onLackOfPermissions));
                int size = missPermissionList.size();
                ActivityCompat.requestPermissions(activity, missPermissionList.toArray(new String[size]), requestCode);
            } else {
                if (runnable != null) {
                    runnable.run();
                }
            }
        } else {
            if (runnable != null) {
                runnable.run();
            }
        }
    }

}
