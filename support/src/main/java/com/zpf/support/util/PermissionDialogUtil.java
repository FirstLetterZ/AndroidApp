package com.zpf.support.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.zpf.frame.IDialogDelegate;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.permission.PermissionSettingManager;
import com.zpf.tool.permission.model.PermissionInfo;
import com.zpf.views.window.IosStyleDialog;

import java.util.List;

/**
 * Created by ZPF on 2018/7/26.
 */
public class PermissionDialogUtil {
    public static final int DIALOG_TYPE_LACK_PERMISSION = 10;
    public static final int DIALOG_TYPE_CANNOT_NOTICE = 11;

    public static void showCannotNoticeDialog(Activity activity, DialogInterface.OnDismissListener listener) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        IDialogDelegate delegate = CentralManager.getInstance(IDialogDelegate.class);
        if (delegate != null) {
            Dialog dialog = delegate.createDialog(activity, null, DIALOG_TYPE_CANNOT_NOTICE);
            if (dialog != null) {
                dialog.setOnDismissListener(listener);
                dialog.show();
                return;
            }
        }
        final String appName = PublicUtil.getAppName(activity);
        final IosStyleDialog hintDialog = new IosStyleDialog(activity);
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
                PermissionSettingManager.get().jumpToNoticeSetting(v.getContext());
            }
        });
        hintDialog.setOnDismissListener(listener);
        try {
            hintDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLackPermissionDialog(@Nullable Activity activity, @Nullable List<PermissionInfo> list,
                                                @Nullable DialogInterface.OnDismissListener listener) {
        if (activity == null || activity.getWindow() == null || list == null || list.size() == 0) {
            return;
        }
        IDialogDelegate delegate = CentralManager.getInstance(IDialogDelegate.class);
        if (delegate != null) {
            Dialog dialog = delegate.createDialog(activity, list, DIALOG_TYPE_LACK_PERMISSION);
            if (dialog != null) {
                dialog.setOnDismissListener(listener);
                dialog.show();
                return;
            }
        }
        final String appName = PublicUtil.getAppName(activity);
        final IosStyleDialog settingDialog = new IosStyleDialog(activity);
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
        settingDialog.setOnDismissListener(listener);
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
                PermissionSettingManager.get().jumpToPermissionSetting(v.getContext());
                settingDialog.dismiss();
            }
        });
        settingDialog.getCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionSettingManager.get().jumpToAppSetting(v.getContext());
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