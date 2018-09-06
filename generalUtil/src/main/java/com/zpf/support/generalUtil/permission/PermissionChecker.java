package com.zpf.support.generalUtil.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/8/22.
 */
public class PermissionChecker {
    private List<PermissionInfo> permissionList;
    public static final int REQ_PERMISSION_CODE = 10001;
    protected final SparseArray<Pair<Runnable, Runnable>> permissionCallBack = new SparseArray<>();

    @SuppressLint("InlinedApi")
    private void initPermissionList() {
        permissionList = new LinkedList<>();
        permissionList.add(new PermissionInfo(Manifest.permission.WRITE_CONTACTS, "写入联系人", Manifest.permission_group.CONTACTS));
        permissionList.add(new PermissionInfo(Manifest.permission.READ_CONTACTS, "读取联系人", Manifest.permission_group.CONTACTS));
        permissionList.add(new PermissionInfo(Manifest.permission.GET_ACCOUNTS, "访问账户Gmail列表", Manifest.permission_group.CONTACTS));

        permissionList.add(new PermissionInfo(Manifest.permission.READ_CALL_LOG, "读取通话记录", Manifest.permission_group.PHONE));
        permissionList.add(new PermissionInfo(Manifest.permission.READ_PHONE_STATE, "读取电话状态", Manifest.permission_group.PHONE));
        permissionList.add(new PermissionInfo(Manifest.permission.CALL_PHONE, "拨打电话", Manifest.permission_group.PHONE));
        permissionList.add(new PermissionInfo(Manifest.permission.WRITE_CALL_LOG, "写入通话记录", Manifest.permission_group.PHONE));
        permissionList.add(new PermissionInfo(Manifest.permission.USE_SIP, "使用SIP视频", Manifest.permission_group.PHONE));
        permissionList.add(new PermissionInfo(Manifest.permission.PROCESS_OUTGOING_CALLS, "处理拨出电话", Manifest.permission_group.PHONE));
        permissionList.add(new PermissionInfo(Manifest.permission.ADD_VOICEMAIL, "添加语音邮件", Manifest.permission_group.PHONE));

        permissionList.add(new PermissionInfo(Manifest.permission.READ_CALENDAR, "读取日程信息", Manifest.permission_group.CALENDAR));
        permissionList.add(new PermissionInfo(Manifest.permission.WRITE_CALENDAR, "写入日程信息", Manifest.permission_group.CALENDAR));

        permissionList.add(new PermissionInfo(Manifest.permission.CAMERA, "访问摄像头", Manifest.permission_group.CAMERA));

        permissionList.add(new PermissionInfo(Manifest.permission.BODY_SENSORS, "读取生命体征相关的传感器数据", Manifest.permission_group.SENSORS));

        permissionList.add(new PermissionInfo(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置", Manifest.permission_group.LOCATION));
        permissionList.add(new PermissionInfo(Manifest.permission.ACCESS_COARSE_LOCATION, "获取粗略位置", Manifest.permission_group.LOCATION));


        permissionList.add(new PermissionInfo(Manifest.permission.READ_EXTERNAL_STORAGE, "读取外部存储", Manifest.permission_group.STORAGE));
        permissionList.add(new PermissionInfo(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入外部存储", Manifest.permission_group.STORAGE));

        permissionList.add(new PermissionInfo(Manifest.permission.RECORD_AUDIO, "录音", Manifest.permission_group.MICROPHONE));

        permissionList.add(new PermissionInfo(Manifest.permission.READ_SMS, "读取短信内容", Manifest.permission_group.SMS));
        permissionList.add(new PermissionInfo(Manifest.permission.RECEIVE_WAP_PUSH, "接收Wap Push", Manifest.permission_group.SMS));
        permissionList.add(new PermissionInfo(Manifest.permission.RECEIVE_MMS, "接收彩信", Manifest.permission_group.SMS));
        permissionList.add(new PermissionInfo(Manifest.permission.RECEIVE_SMS, "接收短信", Manifest.permission_group.SMS));
        permissionList.add(new PermissionInfo(Manifest.permission.SEND_SMS, "发送短信", Manifest.permission_group.SMS));
    }

    //获取所有缺失权限的详细描述
    public List<PermissionInfo> getMissInfo(List<String> list) {
        List<PermissionInfo> PermissionInfoList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            if (permissionList == null) {
                initPermissionList();
            }
            if (permissionList != null) {
                for (String name : list) {
                    for (PermissionInfo permissionInfo : permissionList) {
                        if (TextUtils.equals(permissionInfo.getPermissionName(), name)) {
                            PermissionInfoList.add(permissionInfo);
                            break;
                        }
                    }
                }
            }
        }
        return PermissionInfoList;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Pair<Runnable, Runnable> callBack = permissionCallBack.get(requestCode);
        if (callBack == null) {
            return;
        }
        List<String> missPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (permissions.length < i) {
                    missPermissionList.add(permissions[i]);
                }
            }
        }
        if (missPermissionList.size() == 0) {
            if (callBack.first != null) {
                callBack.first.run();
            }
        } else {
            if (callBack.second != null) {
                if (callBack.second instanceof OnLockPermissionRunnable) {
                    ((OnLockPermissionRunnable) callBack.second).getPermissions().clear();
                    ((OnLockPermissionRunnable) callBack.second).getPermissions().addAll(getMissInfo(missPermissionList));
                }
                callBack.second.run();
            }
        }
    }

    public boolean checkToastEnabled(@NonNull Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public Pair<Runnable, Runnable> getPermissionCallBack(int requestCode) {
        return permissionCallBack.get(requestCode);
    }

    public void onDestroy() {
        permissionCallBack.clear();
    }

}
