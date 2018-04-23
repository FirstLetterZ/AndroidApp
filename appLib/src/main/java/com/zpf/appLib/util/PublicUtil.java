package com.zpf.appLib.util;

/**
 * Created by ZPF on 2018/4/17.
 */

public class PublicUtil {

    public <T> Class<T> getClassByName(String name) {
        Class<T> cls = null;
        try {
            cls = (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cls;
    }

    public static void show(String s) {
    }
}
