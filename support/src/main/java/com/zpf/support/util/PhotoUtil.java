package com.zpf.support.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.zpf.tool.FileUtil;
import com.zpf.tool.permission.OnLockPermissionRunnable;
import com.zpf.tool.permission.PermissionInfo;
import com.zpf.frame.IViewContainer;

import java.util.List;

/**
 * 拍照和从相册中选取
 */
public class PhotoUtil {

    public static boolean takePhoto(final IViewContainer viewContainer, final String filePath, final int requestCode) {
        if (viewContainer != null) {
            viewContainer.checkPermissions(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(filePath));
                        viewContainer.startActivityForResult(capIntent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new OnLockPermissionRunnable() {
                @Override
                public void onLock(List<PermissionInfo> list) {
                    PermissionUtil.get().showPermissionRationaleDialog(viewContainer.getCurrentActivity(), list);
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        }
        return false;
    }

    public static boolean takePhoto(Activity activity, String filePath, int requestCode) {
        if (activity != null && PermissionUtil.get()
                .checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(filePath));
            activity.startActivityForResult(capIntent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean takePhoto(Fragment fragment, String filePath, int requestCode) {
        if (fragment != null && PermissionUtil.get()
                .checkPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(filePath));
            fragment.startActivityForResult(capIntent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean selectFromAlbum(final IViewContainer viewContainer, final int requestCode) {
        if (viewContainer != null) {
            viewContainer.checkPermissions(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        String title = "选择图片";
                        Intent intent = Intent.createChooser(albumIntent, title);
                        viewContainer.startActivityForResult(intent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new OnLockPermissionRunnable() {
                @Override
                public void onLock(List<PermissionInfo> list) {
                    PermissionUtil.get().showPermissionRationaleDialog(viewContainer.getCurrentActivity(), list);
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return false;
    }

    public static boolean selectFromAlbum(Activity activity, int requestCode) {
        if (activity != null && PermissionUtil.get()
                .checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            String title = "选择图片";
            Intent intent = Intent.createChooser(albumIntent, title);
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean selectFromAlbum(Fragment fragment, int requestCode) {
        if (fragment != null && PermissionUtil.get()
                .checkPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            String title = "选择图片";
            Intent intent = Intent.createChooser(albumIntent, title);
            fragment.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

}
