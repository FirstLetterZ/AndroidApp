package com.zpf.appLib.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZPF on 2018/4/24.
 */
public class PathInfo implements Parcelable {
    private String temporaryPath;//临时文件
    private String cachePath;//缓存文件
    private String photoPath;//照片文件
    private String downloadPath;//下载文件

    public PathInfo() {
    }

    protected PathInfo(Parcel in) {
        temporaryPath = in.readString();
        cachePath = in.readString();
        photoPath = in.readString();
        downloadPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temporaryPath);
        dest.writeString(cachePath);
        dest.writeString(photoPath);
        dest.writeString(downloadPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PathInfo> CREATOR = new Creator<PathInfo>() {
        @Override
        public PathInfo createFromParcel(Parcel in) {
            return new PathInfo(in);
        }

        @Override
        public PathInfo[] newArray(int size) {
            return new PathInfo[size];
        }
    };

    public String getTemporaryPath() {
        return temporaryPath;
    }

    public void setTemporaryPath(String temporaryPath) {
        this.temporaryPath = temporaryPath;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
