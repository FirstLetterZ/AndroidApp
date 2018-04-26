package com.zpf.appLib.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zpf.appLib.constant.AppConst;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileUtil {
    private static volatile Properties properties;

    public static Properties getProperties() {
        if (properties == null) {
            synchronized (FileUtil.class) {
                if (properties == null) {
                    properties = new Properties();
                }
            }
        }
        return properties;
    }

    /**
     * 从指定文件读取指定字段
     */
    public static String getPropertiesValue(String key, String fileName) {
        try {
            InputStream fileStream = AppConst.instance().getApplication().getAssets().open(fileName);
            getProperties().load(fileStream);
            return getProperties().getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static PathInfo initPathInfo(Context context) {
        PathInfo pathInfo = new PathInfo();
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            pathInfo.setTemporaryPath(externalCacheDir.getAbsolutePath() + "/temporary");
        } else {
            pathInfo.setTemporaryPath(context.getCacheDir().getAbsolutePath() + "/temporary");
        }
        File externalFilesDir = context.getExternalFilesDir("localCache");
        if (externalFilesDir != null) {
            pathInfo.setCachePath(externalFilesDir.getAbsolutePath());
        } else {
            pathInfo.setCachePath(context.getFilesDir().getAbsolutePath() + "/localCache");
        }
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            pathInfo.setPhotoPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
            pathInfo.setDownloadPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        } else {
            pathInfo.setPhotoPath(pathInfo.getCachePath() + "/photo");
            pathInfo.setDownloadPath(pathInfo.getTemporaryPath() + "/download");
        }
        LogUtil.i(pathInfo.toString());
        return pathInfo;
    }

    //inputStream写入到指定文件
    public static void writeToFile(File file, InputStream inputStream) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length = inputStream.read(buffer);
        while (length > 0) {
            fos.write(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        fos.flush();
        inputStream.close();
        fos.close();
    }


    //下载文件
    public static boolean downloadFile(File file, InputStream inputStream) {
        boolean b = false;
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            fos = new FileOutputStream(file);
            bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            b = true;
        } catch (IOException e) {
            LogUtil.e("downloadFile--read&write:" + e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                LogUtil.e("downloadFile--close:" + e);
            }
        }
        return b;
    }

    //获取文件大小
    public static int getFileSize(String path) {
        File file = new File(path);
        return getFileSize(file);
    }

    //获取文件大小
    public static int getFileSize(File file) {
        int size = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            size = fis.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.i("FileSize=" + size);
        return size;
    }

    //获取指定文件
    public static File getFile(String folderPath, String name) {
        File folder = new File(folderPath);
        if (folder.exists()) {
            File file = new File(folder, name);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    //在指定路径下获取或创建文件
    public static File optFile(String folderPath, String name) {
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(file, name);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    //保存图片到文件
    public static void saveBitmapToFile(File file, Bitmap bmp) {
        saveBitmapToFile(file, bmp, Bitmap.CompressFormat.JPEG, 100);
    }

    public static void saveBitmapToFile(File file, Bitmap bmp, Bitmap.CompressFormat format) {
        saveBitmapToFile(file, bmp, format, 100);
    }

    public static void saveBitmapToFile(File file, Bitmap bmp, Bitmap.CompressFormat format, int quality) {
        LogUtil.i("saveBitmapToFile:" + file.getName());
        FileOutputStream fos = null;
        if (bmp.isRecycled()) {
            LogUtil.e("Bitmap is Recycled");
            return;
        }
        try {
            fos = new FileOutputStream(file);
            bmp.compress(format, quality, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bmp.recycle();
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //删除路径所有文件
    public static void delAllFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    file.delete();
                } else {
                    for (File f : files) {
                        delAllFiles(f.getPath());
                    }
                }
            } else {
                file.delete();
            }
        }
    }

    /*获取相册内的图片路径*/
    @SuppressLint("NewApi")
    public static String fromPhotoAlbum(Context context, final Uri uri) {
        if (context == null || uri == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            /*API19及以上*/
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
