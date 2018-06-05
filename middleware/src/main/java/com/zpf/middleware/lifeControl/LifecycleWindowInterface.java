package com.zpf.middleware.lifeControl;

/**
 * Created by ZPF on 2018/6/5.
 */

public interface LifecycleWindowInterface {

    void show();

    void dismiss();

    void addController(LifecycleArrayListener listener);
}
