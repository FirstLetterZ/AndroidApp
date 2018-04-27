package com.zpf.appLib.util;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.zpf.appLib.base.BaseViewContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/4/26.
 */

public abstract class PermissionHelper {
    private List<String> missPermissionList = new ArrayList<>();//暂时
    private List<String> needRationaleList = new ArrayList<>();//暂时
    private List<String> necessaryPermissionList = new ArrayList<>();//暂时
    private SparseArray<Runnable> callBackArray = new SparseArray<>();

    public boolean hasPermission(BaseViewContainer container, String[] necessaryPermissions,
                                   String[] otherPermissions, int requestCode, Runnable callBack) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            missPermissionList.clear();
            needRationaleList.clear();
            necessaryPermissionList.clear();
            for (String per : necessaryPermissions) {
                if (container.shouldShowRequestPermissionRationale(per)) {
                    needRationaleList.add(per);
                    necessaryPermissionList.add(per);
                } else if (container.getContext().checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                    necessaryPermissionList.add(per);
                }
            }
            for (String per : otherPermissions) {
                if (container.getContext().checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                }
            }
            if (needRationaleList.size() > 0) {
                result = false;
                callBackArray.put(requestCode, callBack);
                List<String> permissionList = new ArrayList<>();
                permissionList.addAll(needRationaleList);
                if (missPermissionList.size() > 0) {
                    permissionList.addAll(missPermissionList);
                }
                showDialog(permissionList);
            } else if (missPermissionList.size() > 0) {
                result = false;
                callBackArray.put(requestCode, callBack);
                container.requestPermission(missPermissionList.toArray(new String[]{}), requestCode,this);
            }
        }
        if (result && callBack != null) {
            callBack.run();
        }
        return result;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        needRationaleList.clear();
        if (necessaryPermissionList.size() > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (necessaryPermissionList.size() > 0) {
                        for (int j = 0; j < necessaryPermissionList.size(); j++) {
                            if (TextUtils.equals(necessaryPermissionList.get(j), permissions[i])) {
                                necessaryPermissionList.remove(j);
                            }
                        }
                    }
                } else {
                    needRationaleList.add(permissions[i]);
                }
            }
        }
        if (necessaryPermissionList.size() == 0) {
            if (callBackArray.get(requestCode) != null) {
                callBackArray.get(requestCode).run();
                callBackArray.remove(requestCode);
            }
        } else {
            showDialog(needRationaleList);
        }

    }

    public abstract void showDialog(List<String> needRationalePermissionList);
}
