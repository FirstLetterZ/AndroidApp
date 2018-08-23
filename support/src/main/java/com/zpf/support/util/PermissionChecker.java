package com.zpf.support.util;

import android.Manifest;
import android.os.Build;

import com.zpf.support.interfaces.OnLackOfPermissions;
import com.zpf.support.interfaces.PermissionCheckerInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZPF on 2018/8/22.
 */
public class PermissionChecker {

    private PermissionCheckerInterface permissionChecker;
    private List<Permission> permissionList;
    private List<String> needRationaleList = new LinkedList<>();
    private List<String> missPermissionList = new LinkedList<>();
    private final int REQ_PERMISSION_CODE = 10001;

    public boolean checkPermissions(String... permissions) {
        return checkPermissions(REQ_PERMISSION_CODE, permissions);
    }

    public boolean checkPermissions(int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {

        } else {
            return true;
        }
    }

    public void checkPermissions(Runnable runnable, OnLackOfPermissions onLackOfPermissions, String... permissions) {
        checkPermissions(runnable, onLackOfPermissions, REQ_PERMISSION_CODE, permissions);
    }

    public void checkPermissions(Runnable runnable, OnLackOfPermissions onLackOfPermissions, int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {

        } else {
            runnable.run();
        }
    }

    class Permission {
        private String permissionName;
        private String permissionDescription;
        private String permissionGroup;

        public Permission(String permissionName, String permissionDescription, String permissionGroup) {
            this.permissionName = permissionName;
            this.permissionDescription = permissionDescription;
            this.permissionGroup = permissionGroup;
        }

        public String getPermissionName() {
            return permissionName;
        }

        public String getPermissionDescription() {
            return permissionDescription;
        }

        public String getPermissionGroup() {
            return permissionGroup;
        }
    }

    private void initPermissionList() {
        permissionList = new LinkedList<>();
        permissionList.add(new Permission(Manifest.permission.WRITE_CONTACTS, "写入联系人", Manifest.permission_group.CONTACTS));
        permissionList.add(new Permission(Manifest.permission.READ_CONTACTS, "读取联系人", Manifest.permission_group.CONTACTS));
        permissionList.add(new Permission(Manifest.permission.GET_ACCOUNTS, "访问账户Gmail列表", Manifest.permission_group.CONTACTS));

        permissionList.add(new Permission(Manifest.permission.READ_CALL_LOG, "读取通话记录", Manifest.permission_group.PHONE));
        permissionList.add(new Permission(Manifest.permission.READ_PHONE_STATE, "读取电话状态", Manifest.permission_group.PHONE));
        permissionList.add(new Permission(Manifest.permission.CALL_PHONE, "拨打电话", Manifest.permission_group.PHONE));
        permissionList.add(new Permission(Manifest.permission.WRITE_CALL_LOG, "写入通话记录", Manifest.permission_group.PHONE));
        permissionList.add(new Permission(Manifest.permission.USE_SIP, "使用SIP视频", Manifest.permission_group.PHONE));
        permissionList.add(new Permission(Manifest.permission.PROCESS_OUTGOING_CALLS, "处理拨出电话", Manifest.permission_group.PHONE));
        permissionList.add(new Permission(Manifest.permission.ADD_VOICEMAIL, "添加语音邮件", Manifest.permission_group.PHONE));

        permissionList.add(new Permission(Manifest.permission.READ_CALENDAR, "读取日程信息", Manifest.permission_group.CALENDAR));
        permissionList.add(new Permission(Manifest.permission.WRITE_CALENDAR, "写入日程信息", Manifest.permission_group.CALENDAR));

        permissionList.add(new Permission(Manifest.permission.CAMERA, "访问摄像头", Manifest.permission_group.CAMERA));

        permissionList.add(new Permission(Manifest.permission.BODY_SENSORS, "读取生命体征相关的传感器数据", Manifest.permission_group.SENSORS));

        permissionList.add(new Permission(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置", Manifest.permission_group.LOCATION));
        permissionList.add(new Permission(Manifest.permission.ACCESS_COARSE_LOCATION, "获取粗略位置", Manifest.permission_group.LOCATION));


        permissionList.add(new Permission(Manifest.permission.READ_EXTERNAL_STORAGE, "读取外部存储", Manifest.permission_group.STORAGE));
        permissionList.add(new Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入外部存储", Manifest.permission_group.STORAGE));

        permissionList.add(new Permission(Manifest.permission.RECORD_AUDIO, "录音", Manifest.permission_group.MICROPHONE));

        permissionList.add(new Permission(Manifest.permission.READ_SMS, "读取短信内容", Manifest.permission_group.SMS));
        permissionList.add(new Permission(Manifest.permission.RECEIVE_WAP_PUSH, "接收Wap Push", Manifest.permission_group.SMS));
        permissionList.add(new Permission(Manifest.permission.RECEIVE_MMS, "接收彩信", Manifest.permission_group.SMS));
        permissionList.add(new Permission(Manifest.permission.RECEIVE_SMS, "接收短信", Manifest.permission_group.SMS));
        permissionList.add(new Permission(Manifest.permission.SEND_SMS, "发送短信", Manifest.permission_group.SMS));
    }
}
