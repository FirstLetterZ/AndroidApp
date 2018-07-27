package com.zpf.support.interfaces;

import android.support.annotation.NonNull;

/**
 * 使用当前application继承此接口，然后使用处通过获取application强转获取内容
 * Created by ZPF on 2018/7/27.
 */
public interface GlobalSettingInterface {
    void initRootLayout(@NonNull RootLayoutInterface rootLayout);

    //预留
    void otherInit(Object object);
}
