package com.zpf.middleware.view;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zpf.appLib.util.JumpPermissionManagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZPF on 2018/4/27.
 */

public class PermissionDialog extends CommonDialog {
    public static String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static String PERMISSION_SDCARD = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static String PERMISSION_PHONE_SATE = Manifest.permission.READ_PHONE_STATE;
    public static String PERMISSION_CALL = Manifest.permission.CALL_PHONE;
    public static String PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static String PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Map<String, String> rationaleMap = new HashMap<>();
    private CommonDialog rationaleDialog;
    private JumpPermissionManagement jumpPermissionManagement;


    public PermissionDialog(@NonNull Context context) {
        super(context);
    }

    public PermissionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PermissionDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
