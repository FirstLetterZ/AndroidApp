package com.zpf.support.interfaces;

import android.support.annotation.NonNull;

/**
 * 使用当前application继承此接口，然后使用处通过获取application强转获取内容
 * Created by ZPF on 2018/7/27.
 */
public interface GlobalConfigInterface {
    /**
     * @param object 需要处理的对象
     */
    void objectInit(@NonNull Object object);

    /**
     * @param object     调用的对象
     * @param methodName 需要处理的方法
     * @param args       参数集合
     * @return
     */
    Object invokeMethod(Object object, String methodName, Object... args);
}
