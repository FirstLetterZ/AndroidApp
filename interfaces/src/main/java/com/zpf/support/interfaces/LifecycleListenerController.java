package com.zpf.support.interfaces;

import com.zpf.support.interfaces.constant.LifecycleState;

/**
 * 常用的有生命周的监听器控制
 * Created by ZPF on 2018/6/28.
 */
public interface LifecycleListenerController {
    //获取当前状态
    @LifecycleState
    int getState();

    //已创建到销毁之间的状态
    boolean isLiving();

    //可交互的状态
    boolean isActive();

    //绑定生命周期的弹窗
    void show(final SafeWindowInterface window);

    //关闭当前弹窗
    boolean dismiss();

    //绑定生命周期的网络请求控制器
    CallBackManagerInterface getCallBackManager();

    void addLifecycleListener(LifecycleInterface lifecycleListener);

    void removeLifecycleListener(LifecycleInterface lifecycleListener);

    void addOnDestroyListener(OnDestroyListener listener);

    void removeOnDestroyListener(OnDestroyListener listener);

    void addResultCallBackListener(ResultCallBackListener callBackListener);

    void removeResultCallBackListener(ResultCallBackListener callBackListener);
}
