package com.zpf.support.api;

import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface ContainerProcessorInterface extends LifecycleInterface, ResultCallBackListener {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(Runnable runnable, Runnable onLack, String... permissions);

    <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener);

}
