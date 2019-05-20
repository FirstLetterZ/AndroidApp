package com.zpf.support.single;

import com.zpf.frame.INavigator;
import com.zpf.support.base.ViewProcessor;

import java.util.HashMap;

/**
 * Created by ZPF on 2019/5/20.
 */
public class ShareBox {
    private static final HashMap<String, INavigator<Class<? extends ViewProcessor>>> realFragmentStackManager = new HashMap<>();

    public static void putRealNavigator(String key, INavigator<Class<? extends ViewProcessor>> navigator) {
        if (key == null || navigator == null) {
            return;
        }
        realFragmentStackManager.put(key, navigator);
    }

    public static INavigator<Class<? extends ViewProcessor>> getRealNavigator(String key) {
        if (key == null) {
            return null;
        }
        return realFragmentStackManager.get(key);
    }
}
