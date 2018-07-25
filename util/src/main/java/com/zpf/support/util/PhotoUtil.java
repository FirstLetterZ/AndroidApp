package com.zpf.support.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.zpf.support.interfaces.ViewContainerInterface;

public class PhotoUtil {

    public static boolean takePhoto(ViewContainerInterface viewContainer, String filePath, int requestCode) {
        if (viewContainer != null) {
            if (viewContainer instanceof Fragment) {
                return takePhoto(((Fragment) viewContainer), filePath, requestCode);
            } else if (viewContainer instanceof Activity) {
                return takePhoto(((Activity) viewContainer), filePath, requestCode);
            }
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

    public static boolean selectFromAlbum(ViewContainerInterface viewContainer, int requestCode) {
        if (viewContainer != null) {
            if (viewContainer instanceof Fragment) {
                return selectFromAlbum(((Fragment) viewContainer), requestCode);
            } else if (viewContainer instanceof Activity) {
                return selectFromAlbum(((Activity) viewContainer), requestCode);
            }
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
