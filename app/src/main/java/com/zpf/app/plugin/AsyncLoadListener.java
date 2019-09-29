package com.zpf.app.plugin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface AsyncLoadListener {
    void onState(@AsyncLoadState int sateCode, @NonNull String targetName, @Nullable PluginApkBean apkBean);

    void onResult(@NonNull String targetName, @NonNull  Class<?> result);
}