package com.zpf.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2022/1/6.
 */
public interface ICacheManager {

    @Nullable
    Object search(@NonNull String key);

    void update(@NonNull String key,@Nullable Object value);
}