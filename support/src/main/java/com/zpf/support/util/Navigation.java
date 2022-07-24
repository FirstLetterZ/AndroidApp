package com.zpf.support.util;

import android.content.Context;
import android.content.Intent;


import com.zpf.api.INavigator;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.model.SmartNavigator;

public class Navigation {
    private volatile static SmartNavigator smartNavigator = null;

    private Navigation() {
    }

    /**
     * @return 不应被保存，应在使用时获取，在使用后释放
     */
    public static INavigator<Class<? extends IViewProcessor>, Intent, Intent> get(Context context) {
        SmartNavigator navigator = getSmartNavigator();
        navigator.setExecutor(context);
        return navigator;
    }

    public static INavigator<Class<? extends IViewProcessor>, Intent, Intent> get(IViewContainer container) {
        SmartNavigator navigator = getSmartNavigator();
        navigator.setExecutor(container);
        return navigator;
    }

    public static INavigator<Class<? extends IViewProcessor>, Intent, Intent> get(androidx.fragment.app.Fragment fragment) {
        SmartNavigator navigator = getSmartNavigator();
        navigator.setExecutor(fragment);
        return navigator;
    }

    public static INavigator<Class<? extends IViewProcessor>, Intent, Intent> get(android.app.Fragment fragment) {
        SmartNavigator navigator = getSmartNavigator();
        navigator.setExecutor(fragment);
        return navigator;
    }

    private static SmartNavigator getSmartNavigator() {
        if (smartNavigator == null) {
            synchronized (Navigation.class) {
                if (smartNavigator == null) {
                    smartNavigator = new SmartNavigator();
                }
            }
        }
        return smartNavigator;
    }

}
