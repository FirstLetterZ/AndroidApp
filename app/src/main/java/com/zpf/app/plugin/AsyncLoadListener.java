package com.zpf.app.plugin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface AsyncLoadListener {
    void onState(@AsyncLoadState int sateCode, @NonNull String targetName, @Nullable PluginApkBean apkBean);

    void onResult(@NonNull String targetName, @NonNull  Class<?> result);
}