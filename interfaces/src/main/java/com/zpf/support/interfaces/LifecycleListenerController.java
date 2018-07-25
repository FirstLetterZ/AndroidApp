package com.zpf.support.interfaces;

import com.zpf.support.data.constant.LifecycleState;

/**
 * 常用的有生命周的监听器控制
 * Created by ZPF on 2018/6/28.
 */
public interface LifecycleListenerController {
    //获取当前状态
    @LifecycleState
    int getState();

    //绑定生命周期的弹窗
    void show(SafeWindowInterface window);

    //绑定生命周期的网络请求控制器
    CallBackManagerInterface getCallBackManager();

    void addLifecycleListener(LifecycleInterface lifecycleListener);

    void removeLifecycleListener(LifecycleInterface lifecycleListener);

    void addOnDestroyListener(OnDestroyListener listener);

    void removeOnDestroyListener(OnDestroyListener listener);

    void addResultCallBackListener(ResultCallBackListener callBackListener);

    void removeResultCallBackListener(ResultCallBackListener callBackListener);
}
