package com.zpf.support.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.zpf.api.IPermissionResult;
import com.zpf.tool.FileUtil;
import com.zpf.tool.PublicUtil;
import com.zpf.frame.IViewContainer;
import com.zpf.support.R;
import com.zpf.tool.permission.PermissionDescription;

import java.util.List;

/**
 * 拍照和从相册中选取
 */
public class PhotoUtil {

    public static boolean takePhoto(final IViewContainer viewContainer, final String filePath, final int requestCode) {
        if (viewContainer != null) {
            viewContainer.checkPermissions(new IPermissionResult() {
                @Override
                public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                    if (missPermissions == null || missPermissions.size() == 0) {
                        try {
                            Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(viewContainer.getContext(), filePath));
                            viewContainer.startActivityForResult(capIntent, requestCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        PermissionUtil.get().showPermissionRationaleDialog(
                                viewContainer.getCurrentActivity(), PermissionDescription.get().queryMissInfo(missPermissions));
                    }
                }
            }, requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
        }
        return false;
    }

    public static boolean takePhoto(Activity activity, String filePath, int requestCode) {
        if (activity != null && PermissionUtil.get()
                .checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            capIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(activity, filePath));
            activity.startActivityForResult(capIntent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean takePhoto(androidx.fragment.app.Fragment fragment, String filePath, int requestCode) {
        if (fragment != null && PermissionUtil.get()
                .checkPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            capIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(fragment.getContext(), filePath));
            fragment.startActivityForResult(capIntent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean takePhoto(android.app.Fragment fragment, String filePath, int requestCode) {
        if (fragment != null && PermissionUtil.get()
                .checkPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            capIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtil.getUri(fragment.getActivity(), filePath));
            fragment.startActivityForResult(capIntent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean selectFromAlbum(final IViewContainer viewContainer, final int requestCode) {
        if (viewContainer != null) {
            viewContainer.checkPermissions(new IPermissionResult() {
                @Override
                public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                    if (missPermissions == null || missPermissions.size() == 0) {
                        try {
                            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            String title = PublicUtil.getString(R.string.select_photo);
                            Intent intent = Intent.createChooser(albumIntent, title);
                            viewContainer.startActivityForResult(intent, requestCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        PermissionUtil.get().showPermissionRationaleDialog(
                                viewContainer.getCurrentActivity(), PermissionDescription.get().queryMissInfo(missPermissions));
                    }
                }
            }, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return false;
    }

    public static boolean selectFromAlbum(Activity activity, int requestCode) {
        if (activity != null && PermissionUtil.get()
                .checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            String title = PublicUtil.getString(R.string.select_photo);
            Intent intent = Intent.createChooser(albumIntent, title);
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean selectFromAlbum(androidx.fragment.app.Fragment fragment, int requestCode) {
        if (fragment != null && PermissionUtil.get()
                .checkPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            String title = PublicUtil.getString(R.string.select_photo);
            Intent intent = Intent.createChooser(albumIntent, title);
            fragment.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean selectFromAlbum(android.app.Fragment fragment, int requestCode) {
        if (fragment != null && PermissionUtil.get()
                .checkPermission(fragment, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            String title = PublicUtil.getString(R.string.select_photo);
            Intent intent = Intent.createChooser(albumIntent, title);
            fragment.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }
}
