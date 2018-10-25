package com.zpf.tool.permission;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用android.app.Fragment检查权限
 * Created by ZPF on 2018/8/22.
 */
public class FragmentPermissionChecker extends PermissionChecker {
    public boolean checkPermissions(@NonNull Fragment fragment, String... permissions) {
        return checkPermissions(fragment, REQ_PERMISSION_CODE, permissions);
    }

    public boolean checkPermissions(@NonNull Fragment fragment, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (fragment.getContext() == null) {
                return false;
            }
            List<String> missPermissionList = new ArrayList<>();
            for (String per : permissions) {
                if (ActivityCompat.checkSelfPermission(fragment.getContext(), per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                }
            }
            if (missPermissionList.size() > 0) {
                int size = missPermissionList.size();
                fragment.requestPermissions(missPermissionList.toArray(new String[size]), requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void checkPermissions(@NonNull Fragment fragment, Runnable runnable, Runnable onLackOfPermissions, String... permissions) {
        checkPermissions(fragment, runnable, onLackOfPermissions, REQ_PERMISSION_CODE, permissions);
    }

    public void checkPermissions(@NonNull Fragment fragment, Runnable runnable, Runnable onLackOfPermissions, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (fragment.getContext() == null) {
                return;
            }
            List<String> missPermissionList = new ArrayList<>();
            for (String per : permissions) {
                if (ActivityCompat.checkSelfPermission(fragment.getContext(), per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                }
            }
            if (missPermissionList.size() > 0) {
                permissionCallBack.put(requestCode, new Pair<>(runnable, onLackOfPermissions));
                int size = missPermissionList.size();
                fragment.requestPermissions(missPermissionList.toArray(new String[size]), requestCode);
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
