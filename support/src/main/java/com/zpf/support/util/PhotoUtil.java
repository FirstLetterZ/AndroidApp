package com.zpf.support.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.zpf.file.FileUriUtil;
import com.zpf.frame.IViewContainer;
import com.zpf.support.R;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.permission.PermissionDescription;
import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 拍照和从相册中选取
 */
public abstract class PhotoUtil {

    public static boolean takePhoto(IViewContainer viewContainer, final File file, final int requestCode) {
        if (viewContainer == null) {
            return false;
        }
        final WeakReference<IViewContainer> reference = new WeakReference<>(viewContainer);
        return PermissionManager.get().checkPermission(viewContainer, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                IViewContainer container = reference.get();
                if (container == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUriUtil.pathToUri(container.getContext(), file));
                        container.startActivityForResult(capIntent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            container.getCurrentActivity(), PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public static boolean takePhoto(Activity activity, final File file, int requestCode) {
        if (activity == null) {
            return false;
        }
        final WeakReference<Activity> reference = new WeakReference<>(activity);
        return PermissionManager.get().checkPermission(activity, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                Activity currentActivity = reference.get();
                if (currentActivity == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUriUtil.pathToUri(currentActivity, file));
                        currentActivity.startActivityForResult(capIntent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            currentActivity, PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public static boolean takePhoto(androidx.fragment.app.Fragment fragment, final File file, int requestCode) {
        if (fragment == null) {
            return false;
        }
        final WeakReference<androidx.fragment.app.Fragment> reference = new WeakReference<>(fragment);
        return PermissionManager.get().checkPermission(fragment, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                androidx.fragment.app.Fragment currentFragment = reference.get();
                if (currentFragment == null) {
                    return;
                }
                Activity currentActivity = currentFragment.getActivity();
                if (currentActivity == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUriUtil.pathToUri(currentActivity, file));
                        currentFragment.startActivityForResult(capIntent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            currentActivity, PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public static boolean takePhoto(android.app.Fragment fragment, final File file, int requestCode) {
        if (fragment == null) {
            return false;
        }
        final WeakReference<android.app.Fragment> reference = new WeakReference<>(fragment);
        return PermissionManager.get().checkPermission(fragment, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                android.app.Fragment currentFragment = reference.get();
                if (currentFragment == null) {
                    return;
                }
                Activity currentActivity = currentFragment.getActivity();
                if (currentActivity == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        capIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUriUtil.pathToUri(currentActivity, file));
                        currentFragment.startActivityForResult(capIntent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            currentActivity, PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public static boolean selectFromAlbum(final IViewContainer viewContainer, final int requestCode) {
        if (viewContainer == null) {
            return false;
        }
        final WeakReference<IViewContainer> reference = new WeakReference<>(viewContainer);
        return PermissionManager.get().checkPermission(viewContainer, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                IViewContainer container = reference.get();
                if (container == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        String title = PublicUtil.getString(R.string.select_photo);
                        Intent intent = Intent.createChooser(albumIntent, title);
                        container.startActivityForResult(intent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            container.getCurrentActivity(), PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean selectFromAlbum(Activity activity, int requestCode) {
        if (activity == null) {
            return false;
        }
        final WeakReference<Activity> reference = new WeakReference<>(activity);
        return PermissionManager.get().checkPermission(activity, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                Activity currentActivity = reference.get();
                if (currentActivity == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        String title = PublicUtil.getString(R.string.select_photo);
                        Intent intent = Intent.createChooser(albumIntent, title);
                        currentActivity.startActivityForResult(intent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            currentActivity, PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean selectFromAlbum(androidx.fragment.app.Fragment fragment, int requestCode) {
        if (fragment == null) {
            return false;
        }
        final WeakReference<androidx.fragment.app.Fragment> reference = new WeakReference<>(fragment);
        return PermissionManager.get().checkPermission(fragment, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                androidx.fragment.app.Fragment currentFragment = reference.get();
                if (currentFragment == null) {
                    return;
                }
                Activity currentActivity = currentFragment.getActivity();
                if (currentActivity == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        String title = PublicUtil.getString(R.string.select_photo);
                        Intent intent = Intent.createChooser(albumIntent, title);
                        currentFragment.startActivityForResult(intent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            currentActivity, PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean selectFromAlbum(android.app.Fragment fragment, int requestCode) {
        if (fragment == null) {
            return false;
        }
        final WeakReference<android.app.Fragment> reference = new WeakReference<>(fragment);
        return PermissionManager.get().checkPermission(fragment, requestCode, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                android.app.Fragment currentFragment = reference.get();
                if (currentFragment == null) {
                    return;
                }
                Activity currentActivity = currentFragment.getActivity();
                if (currentActivity == null) {
                    return;
                }
                if (missPermissions == null || missPermissions.size() == 0) {
                    try {
                        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        String title = PublicUtil.getString(R.string.select_photo);
                        Intent intent = Intent.createChooser(albumIntent, title);
                        currentFragment.startActivityForResult(intent, requestCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            currentActivity, PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}
