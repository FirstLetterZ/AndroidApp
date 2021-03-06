package com.zpf.support.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;

import com.zpf.api.IPermissionResult;
import com.zpf.support.view.CommonDialog;
import com.zpf.tool.permission.ActivityPermissionChecker;
import com.zpf.tool.permission.PermissionChecker;
import com.zpf.tool.permission.PermissionDescription;
import com.zpf.tool.permission.PermissionInfo;
import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.config.AppContext;
import com.zpf.tool.PublicUtil;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Created by ZPF on 2018/7/26.
 */
public class PermissionUtil implements IPermissionResult {
    private final String appName = PublicUtil.getAppName(AppContext.get());
    private static volatile PermissionUtil instance;
    private final PermissionManager permissionManager = new PermissionManager();
    private final ActivityPermissionChecker activityChecker = new ActivityPermissionChecker();
    private SoftReference<Activity> softReference = null;

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

    public boolean checkPermission(@NonNull androidx.fragment.app.Fragment fragment, @NonNull String... permission) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return false;
        } else {
            return checkPermission(activity, permission);
        }
    }

    public boolean checkPermission(@NonNull android.app.Fragment fragment, @NonNull String... permission) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return false;
        } else {
            return checkPermission(activity, permission);
        }
    }

    public boolean checkPermission(@NonNull Activity activity, @NonNull String... permission) {
        softReference = new SoftReference<>(activity);
        return activityChecker.checkPermissions(activity, PermissionChecker.REQ_PERMISSION_CODE,
                this, permission);
    }

    @Override
    public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
        final SoftReference<Activity> activityReference = softReference;
        softReference = null;
        if (activityReference != null) {
            showPermissionRationaleDialog(activityReference.get(), PermissionDescription.get().queryMissInfo(missPermissions));
        }
    }

    public ActivityPermissionChecker getChecker() {
        return activityChecker;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public boolean checkNoticeEnabled(@NonNull Activity activity, DialogInterface.OnDismissListener listener) {
        boolean isOpen = PermissionChecker.checkNoticeEnabled(activity);
        if (!isOpen && listener != null) {
            showHintDialog(activity, listener);
        }
        return isOpen;
    }

    public void showHintDialog(Activity activity, DialogInterface.OnDismissListener listener) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        final CommonDialog hintDialog = new CommonDialog(activity);
        hintDialog.setCancelable(true);
        hintDialog.setCanceledOnTouchOutside(true);
        hintDialog.getIcon().setVisibility(View.GONE);
        hintDialog.getTitle().setVisibility(View.VISIBLE);
        hintDialog.getTitle().setText("缺少通知权限");
        String msg = "无法收到部分提示信息，请前往“设置-通知”中找到" + appName + "并授予通知权限";
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
                permissionManager.jumpToNoticeSetting(v.getContext());
            }
        });
        hintDialog.setOnDismissListener(listener);
        try {
            hintDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPermissionRationaleDialog(Activity activity, List<PermissionInfo> list) {
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
        for (PermissionInfo permission : list) {
            String expression = permission.getPermissionDescription();
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
                permissionManager.jumpToPermissionSetting(v.getContext());
                settingDialog.dismiss();
            }
        });
        settingDialog.getCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionManager.jumpToAppSetting(v.getContext());
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

}