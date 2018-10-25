package com.zpf.support.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ZPF on 2018/6/13.
 */
public interface LifecycleInterface extends OnDestroyListener {
    void onPreCreate(@Nullable Bundle savedInstanceState);

    void afterCreate(@Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(@NonNull Bundle outState);

    void onRestoreInstanceState(@NonNull Bundle savedInstanceState);

}
