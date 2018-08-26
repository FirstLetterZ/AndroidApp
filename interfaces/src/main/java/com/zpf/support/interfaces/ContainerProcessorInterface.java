package com.zpf.support.interfaces;

/**
 * Created by ZPF on 2018/6/14.
 */
public interface ContainerProcessorInterface extends LifecycleInterface, ResultCallBackListener {
    void runWithPermission(Runnable runnable, String... permissions);

    void runWithPermission(Runnable runnable, Runnable onLack, String... permissions);

}
