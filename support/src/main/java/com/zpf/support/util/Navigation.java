package com.zpf.support.util;

import android.content.Intent;

import com.zpf.api.INavigator;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.model.SmartNavigator;

public class Navigation {
    private volatile static SmartNavigator navigator = null;
    private Navigation() {
    }

    /**
     * @param executor 支持各种Activity或Fragment或其他Context，
     * @return 不应被保存，应在使用时获取，在使用后释放
     */
    public static INavigator<Class<? extends IViewProcessor>, Intent, Intent> get(Object executor) {
        if (navigator == null) {
            synchronized (Navigation.class) {
                if (navigator == null) {
                    navigator = new SmartNavigator();
                }
            }
        }
        navigator.setExecutor(executor);
        return navigator;
    }

}
