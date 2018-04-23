package com.zpf.appLib.view.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by ZPF on 2018/4/13.
 */
public interface LifecycleView {

    void afterCreate(@Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onNewIntent(Intent intent);

    void onVisibleChange(boolean oldFragmentVisible, boolean newFragmentVisible,
                         boolean oldActivityVisible, boolean newActivityVisible);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
