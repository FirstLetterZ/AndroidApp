package com.zpf.middleware.util;

import android.Manifest;
import android.text.TextUtils;
import android.view.View;

import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.constant.AppConst;
import com.zpf.appLib.util.JumpPermissionManagement;
import com.zpf.appLib.util.PermissionHelper;
import com.zpf.appLib.util.SafeClickListener;
import com.zpf.middleware.R;
import com.zpf.middleware.view.CommonDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZPF on 2018/4/27.
 */

public class PermissionUtil extends PermissionHelper {
    public static String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static String PERMISSION_SDCARD = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static String PERMISSION_PHONE_SATE = Manifest.permission.READ_PHONE_STATE;
    public static String PERMISSION_CALL = Manifest.permission.CALL_PHONE;
    public static String PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Map<String, String> rationaleMap = new HashMap<>();
    private CommonDialog rationaleDialog;
    private JumpPermissionManagement jumpPermissionManagement;
    private static volatile PermissionUtil permissionUtil;

    private static PermissionUtil instance() {
        if (permissionUtil == null) {
            synchronized (PermissionUtil.class) {
                if (permissionUtil == null) {
                    permissionUtil = new PermissionUtil();
                }
            }
        }
        return permissionUtil;
    }


    private PermissionUtil() {
        rationaleMap.put(PERMISSION_CAMERA, "缺少照相机权限");
        rationaleMap.put(PERMISSION_SDCARD, "缺少本地读写权限");
        rationaleMap.put(PERMISSION_PHONE_SATE, "缺少获取本机识别码权限");
        rationaleMap.put(PERMISSION_CALL, "缺少拨打电话权限");
        rationaleMap.put(PERMISSION_FINE_LOCATION, "缺少精确定位权限");
        rationaleMap.put(PERMISSION_COARSE_LOCATION, "缺少粗略定位权限");
    }

    public static boolean checkNecessaryPermission(BaseViewContainer container, String[] necessaryPermissions) {
        return instance().hasPermission(container, necessaryPermissions, null,
                AppConst.REQUEST_CODE_CHECK_PERMISSION, null);
    }

    public static boolean checkNecessaryPermission(BaseViewContainer container, String[] necessaryPermissions,
                                                   Runnable successCallBack) {
        return instance().hasPermission(container, necessaryPermissions, null,
                AppConst.REQUEST_CODE_CHECK_PERMISSION, successCallBack);
    }

    public static boolean checkPermission(BaseViewContainer container, String[] necessaryPermissions,
                                          String[] otherPermissions) {
        return instance().hasPermission(container, necessaryPermissions, otherPermissions,
                AppConst.REQUEST_CODE_CHECK_PERMISSION, null);
    }

    public static boolean checkPermission(BaseViewContainer container, String[] necessaryPermissions,
                                          String[] otherPermissions, Runnable successCallBack) {
        return instance().hasPermission(container, necessaryPermissions, otherPermissions,
                AppConst.REQUEST_CODE_CHECK_PERMISSION, successCallBack);
    }

    public static boolean checkPermission(BaseViewContainer container, String[] necessaryPermissions,
                                          String[] otherPermissions, int requestCode, Runnable successCallBack) {
        return instance().hasPermission(container, necessaryPermissions, otherPermissions, requestCode, successCallBack);
    }

    @Override
    public void showDialog(List<String> needRationalePermissionList) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String per : needRationalePermissionList) {
            String rationale = rationaleMap.get(per);
            if (!TextUtils.isEmpty(rationale) && builder.indexOf(rationale) < 0) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(rationale);
                i++;
            }
        }
        if (rationaleDialog == null) {
            rationaleDialog = new CommonDialog(AppConst.instance().getApplication());
            rationaleDialog.getDialogTitle().setText(PublicUtil.getString(R.string.lack_permission));
            rationaleDialog.getSplitLine().setVisibility(View.VISIBLE);
            rationaleDialog.getDialogConfirm().setText(PublicUtil.getString(R.string.go_to_setting));
            rationaleDialog.getDialogConfirm().setVisibility(View.VISIBLE);
            rationaleDialog.getDialogConfirm().setOnClickListener(new SafeClickListener() {
                @Override
                public void click(View v) {
                    if (jumpPermissionManagement == null) {
                        jumpPermissionManagement = new JumpPermissionManagement();
                    }
                    jumpPermissionManagement.goToSetting(v.getContext());
                    rationaleDialog.dismiss();
                }
            });
        }
        if (builder.length() > 0) {
            rationaleDialog.getDialogMessage().setText(builder.toString());
            rationaleDialog.getDialogMessage().setVisibility(View.VISIBLE);
        } else {
            rationaleDialog.getDialogMessage().setVisibility(View.GONE);
        }
        rationaleDialog.show();
    }
}
