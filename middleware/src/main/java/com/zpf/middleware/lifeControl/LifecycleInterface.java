package com.zpf.middleware.lifeControl;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by ZPF on 2018/6/5.
 */
public interface LifecycleInterface extends OnDestroyListener {
    void beforeCreate();

    void afterCreate(@Nullable Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);

}
