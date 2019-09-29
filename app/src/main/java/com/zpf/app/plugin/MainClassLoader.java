package com.zpf.app.plugin;

import com.zpf.api.IClassLoader;

public class MainClassLoader implements IClassLoader {

    @Override
    public Object newInstance(String name, Object... args) {
        return null;
    }

    @Override
    public Class<?> getClass(String name) {
        return null;
    }

}