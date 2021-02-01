package com.zpf.app.plugin;

import android.widget.TextView;

import com.zpf.api.IClassLoader;

public class MainClassLoader implements IClassLoader {

    @Override
    public Object newInstance(String name, Object... args) {
        return null;
    }

    @Override
    public Class<?> getClass(String name) {
        if("aaa".equals(name)){
            return TextView.class;
        }
        return null;
    }

}