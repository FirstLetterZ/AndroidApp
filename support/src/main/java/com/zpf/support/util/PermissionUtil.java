package com.zpf.support.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.zpf.support.generalUtil.AppContext;
import com.zpf.support.generalUtil.JumpPermissionManagement;
import com.zpf.support.interfaces.ViewContainerInterface;
import com.zpf.support.view.CommonDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZPF on 2018/7/26.
 */

public class PermissionUtil {
    private OnPermissionResult permissionResult;
    private final String appName = PublicUtil.getAppName(AppContext.get());
    private final int REQ_PERM = 222;
    private static final Map<String, String> expressionMap = new HashMap<>();//权限描述
    private static volatile PermissionUtil instance;

    public static PermissionUtil get() {
        if (instance == null) {
            synchronized (PermissionUtil.class) {
                if (instance == null) {
                    instance = new PermissionUtil();
                }
            }
        }
        return instance;
    }

    public PermissionUtil() {
        initExpressionMap();
    }

    private void initExpressionMap() {
        if (expressionMap.size() == 0) {
            expressionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "内存读写权限");
            expressionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, "内存读写权限");
            expressionMap.put(Manifest.permission.READ_PHONE_STATE, "电话权限");
            expressionMap.put(Manifest.permission.CALL_PHONE, "电话权限");
            expressionMap.put(Manifest.permission.CAMERA, "相机权限");
            expressionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, "获取当前位置权限");
            expressionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, "获取当前位置权限");
            expressionMap.put(Manifest.permission.WRITE_SETTINGS, "允许修改系统设置");
        }
    }

    public boolean checkPermission(@NonNull Activity activity, @NonNull String... permission) {
        List<String> needRationaleList = new ArrayList<>();
        List<String> missPermissionList = new ArrayList<>();
        for (String per : permission) {
            if (ActivityCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                if (!SpUtil.getBoolean(per)) {
                    missPermissionList.add(per);
                    SpUtil.putValue(per, true);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, per)) {
                        missPermissionList.add(per);
                    } else {
                        needRationaleList.add(per);
                    }
                }
            }
        }
        if (needRationaleList.size() > 0) {
            showPermissionRationaleDialog(activity, needRationaleList);
            return false;
        } else if (missPermissionList.size() > 0) {
            int size = missPermissionList.size();
            ActivityCompat.requestPermissions(activity, missPermissionList.toArray(new String[size]), REQ_PERM);
            return false;
        } else {
            return true;
        }
    }

    public boolean checkPermission(@NonNull android.support.v4.app.Fragment fragment, @NonNull String... permission) {
        List<String> needRationaleList = new ArrayList<>();
        List<String> missPermissionList = new ArrayList<>();
        for (String per : permission) {
            if (fragment.getContext() != null &&
                    ActivityCompat.checkSelfPermission(fragment.getContext(), per) != PackageManager.PERMISSION_GRANTED) {
                if (!SpUtil.getBoolean(per)) {
                    missPermissionList.add(per);
                    SpUtil.putValue(per, true);
                } else {
                    if (fragment.shouldShowRequestPermissionRationale(per)) {
                        missPermissionList.add(per);
                    } else {
                        needRationaleList.add(per);
                    }
                }
            }
        }
        if (needRationaleList.size() > 0) {
            showPermissionRationaleDialog(fragment.getActivity(), needRationaleList);
            return false;
        } else if (missPermissionList.size() > 0) {
            int size = missPermissionList.size();
            fragment.requestPermissions(missPermissionList.toArray(new String[size]), REQ_PERM);
            return false;
        } else {
            return true;
        }
    }

    public boolean checkPermission(ViewContainerInterface viewContainer, @NonNull String... permission) {
        return checkPermission(viewContainer, null, permission);
    }

    public synchronized boolean checkPermission(ViewContainerInterface viewContainer, OnPermissionResult permissionResult,
                                                @NonNull String... permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            this.permissionResult = permissionResult;
            List<String> missPermissionList = new ArrayList<>();
            for (String per : permission) {
                if (ActivityCompat.checkSelfPermission(viewContainer.getContext(), per) != PackageManager.PERMISSION_GRANTED) {
                    missPermissionList.add(per);
                }
            }
            if (missPermissionList.size() > 0) {
                this.permissionResult = permissionResult;
                int size = missPermissionList.size();
                if (viewContainer instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) viewContainer, missPermissionList.toArray(new String[size]), REQ_PERM);
                } else if (viewContainer instanceof android.support.v4.app.Fragment) {
                    ((android.support.v4.app.Fragment) viewContainer).requestPermissions(missPermissionList.toArray(new String[size]), REQ_PERM);
                } else if (viewContainer instanceof android.app.Fragment) {
                    ((android.app.Fragment) viewContainer).requestPermissions(missPermissionList.toArray(new String[size]), REQ_PERM);
                }
                return false;
            } else {
                if (permissionResult != null) {
                    permissionResult.onSuccess();
                }
                return true;
            }
        } else {
            if (permissionResult != null) {
                permissionResult.onSuccess();
            }
            return true;
        }
    }

    public void onRequestPermissionsResult(ViewContainerInterface viewContainer, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> missPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (permissions.length < i) {
                    missPermissionList.add(permissions[i]);
                }
            }
        }
        if (missPermissionList.size() == 0) {
            if (permissionResult != null) {
                permissionResult.onSuccess();
            }
        } else {
            if (viewContainer != null) {
                if (viewContainer instanceof Activity) {
                    showPermissionRationaleDialog((Activity) viewContainer, missPermissionList);
                } else if (viewContainer instanceof android.support.v4.app.Fragment) {
                    showPermissionRationaleDialog(((android.support.v4.app.Fragment) viewContainer).getActivity(), missPermissionList);
                } else if (viewContainer instanceof android.app.Fragment) {
                    showPermissionRationaleDialog(((android.app.Fragment) viewContainer).getActivity(), missPermissionList);
                }
            }
        }
    }

    public boolean checkToastEnabled(Context context) {
        if (context == null) {
            return true;
        }
        boolean isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        if (!isOpened && context instanceof Activity) {
            showHintDialog((Activity) context);
        }
        return isOpened;
    }

    private void showHintDialog(Activity activity) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        final CommonDialog hintDialog = new CommonDialog(activity);
        hintDialog.setCancelable(true);
        hintDialog.setCanceledOnTouchOutside(true);
        hintDialog.getIcon().setVisibility(View.GONE);
        hintDialog.getTitle().setVisibility(View.VISIBLE);
        hintDialog.getTitle().setText("缺少通知权限");
        String msg = "无法收到部分弹窗信息，请前往“设置-通知”中找到" + appName + "并授予通知权限";
        hintDialog.getMessage().setText(msg);
        hintDialog.getViewLine().setVisibility(View.VISIBLE);
        hintDialog.getCancel().setText("下次再说");
        hintDialog.getCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintDialog.dismiss();
            }
        });
        hintDialog.getConfirm().setText("去设置");
        hintDialog.getConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintDialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", v.getContext().getPackageName());
                    intent.putExtra("app_uid", v.getContext().getApplicationInfo().uid);
                    v.getContext().startActivity(intent);
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + v.getContext().getPackageName()));
                    v.getContext().startActivity(intent);
                }
            }
        });
        try {
            hintDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPermissionRationaleDialog(Activity activity, List<String> list) {
        if (list == null || list.size() == 0 || activity == null || activity.getWindow() == null) {
            return;
        }
        final CommonDialog settingDialog = new CommonDialog(activity);
        settingDialog.getIcon().setVisibility(View.GONE);
        settingDialog.getInput().setVisibility(View.GONE);
        settingDialog.getTitle().setVisibility(View.VISIBLE);
        settingDialog.getMessage().setVisibility(View.VISIBLE);
        settingDialog.getViewLine().setVisibility(View.VISIBLE);
        settingDialog.getConfirm().setVisibility(View.VISIBLE);
        settingDialog.getCancel().setVisibility(View.VISIBLE);
        settingDialog.getTitle().setText("缺少必要的权限");
        settingDialog.getCancel().setText("去应用管理");
        settingDialog.getCancel().setTextColor(Color.parseColor("#647bff"));
        settingDialog.getConfirm().setText("去权限列表");
        settingDialog.getConfirm().setTextColor(Color.parseColor("#647bff"));
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String permission : list) {
            String expression = expressionMap.get(permission);
            if (!TextUtils.isEmpty(expression) && builder.indexOf(expression) < 0) {
                if (i > 0) {
                    builder.append("、");
                }
                builder.append(expression);
                i++;
            }
        }
        builder.append("\n请允许").append(appName).append("使用相关服务");
        settingDialog.getMessage().setText(builder.toString());
        settingDialog.getConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JumpPermissionManagement().goToSetting(v.getContext());
                settingDialog.dismiss();
            }
        });
        settingDialog.getCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageURI = Uri.parse("package:" + settingDialog.getContext().getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                settingDialog.getContext().startActivity(intent);
                settingDialog.dismiss();
            }
        });
        settingDialog.setCancelable(true);
        settingDialog.setCanceledOnTouchOutside(true);
        try {
            settingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public void getDeviceId(final ViewContainerInterface viewContainer, final DeviceIdListener listener) {
        if (viewContainer == null || viewContainer.getContext() == null || listener == null) {
            return;
        }
        checkPermission(viewContainer, new OnPermissionResult() {

            @Override
            public void onSuccess() {
                String result;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    result = Build.getSerial();
                } else {
                    result = Build.SERIAL;
                }
                if (TextUtils.isEmpty(result) || "unknown".equalsIgnoreCase(result)) {
                    if (viewContainer != null && viewContainer.getContext() != null) {
                        TelephonyManager telephonyManager = (TelephonyManager) viewContainer.getContext()
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        if (telephonyManager != null) {
                            result = telephonyManager.getDeviceId();
                        }
                    }
                }
                listener.onSuccess(result);
            }
        }, Manifest.permission.READ_PHONE_STATE);
    }

    public interface OnPermissionResult {
        void onSuccess();
    }

    public interface DeviceIdListener {
        void onSuccess(String deviceId);
    }
}