package com.zpf.support.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.View;

import com.zpf.support.data.constant.AppContext;
import com.zpf.support.data.util.SpUtil;
import com.zpf.support.interfaces.ViewContainerInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZPF on 2018/6/12.
 */
public class PermissionUtil {
    private final static int REQ_PERM = 101;
    private final String appName = PublicUtil.getAppName(AppContext.get());
    private CommonDialog hintDialog;
    private CommonDialog settingDialog;
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

    private PermissionUtil() {
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

    public boolean checkPermission(@NonNull ViewContainerInterface container, @NonNull String... permission) {
        if (container instanceof BaseFragment) {
            return checkPermission((Fragment) container, permission);
        } else {
            return container instanceof BaseActivity && checkPermission((Activity) container, permission);
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

    public boolean checkPermission(@NonNull Fragment fragment, @NonNull String... permission) {
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

    public void showPermissionRationaleDialog(Activity activity, List<String> list) {
        if (list == null || list.size() == 0 || activity == null || activity.getWindow() == null || !activity.getWindow().isActive()) {
            return;
        }
        settingDialog = new CommonDialog(activity);
        settingDialog.getCommonIcon().setVisibility(View.GONE);
        settingDialog.getTitle().setVisibility(View.VISIBLE);
        settingDialog.getMessage().setVisibility(View.VISIBLE);
        settingDialog.getViewLine().setVisibility(View.VISIBLE);
        settingDialog.getOk().setVisibility(View.VISIBLE);
        settingDialog.getTitle().setText("缺少必要的权限");
        settingDialog.getCancel().setText("去应用管理");
        settingDialog.getOk().setText("去权限列表");
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String permission : list) {
            String expression = expressionMap.get(permission);
            if (!TextUtils.isEmpty(expression) && builder.indexOf(expression) < 0) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(expression);
                i++;
            }
        }
        builder.append("\n请进入系统设置或权限管理工具内,并允许").append(appName).append("使用相关服务");
        settingDialog.getMessage().setText(builder.toString());
        settingDialog.getOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JumpPermissionManagement().goToSetting(settingDialog.getContext());
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
        settingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                settingDialog = null;
            }
        });
        settingDialog.setCancelable(true);
        settingDialog.setCanceledOnTouchOutside(true);
        try {
            if (!settingDialog.isShowing()) {
                settingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkToastEnabled(Activity activity, OnCheckNotificationListener listener) {
        boolean isOpened = NotificationManagerCompat.from(activity).areNotificationsEnabled();
        if (!isOpened) {
            showHintDialog(activity, listener);
        }
        return isOpened;
    }

    private void showHintDialog(Activity activity, final OnCheckNotificationListener listener) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        if (hintDialog == null) {
            hintDialog = new CommonDialog(activity);
            hintDialog.setCancelable(true);
            hintDialog.setCanceledOnTouchOutside(true);
            hintDialog.getCommonIcon().setVisibility(View.GONE);
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
                    if (listener != null) {
                        listener.cancel();
                    }
                }
            });
            hintDialog.getOk().setText("去设置");
            hintDialog.getOk().setOnClickListener(new View.OnClickListener() {
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
                    if (listener != null) {
                        listener.goSetting();
                    }
                }
            });
            hintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hintDialog = null;
                }
            });
        }
        try {
            if (!hintDialog.isShowing()) {
                hintDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnCheckNotificationListener {
        void cancel();

        void goSetting();
    }

}
