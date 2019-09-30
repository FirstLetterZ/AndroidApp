package com.example.aplugin;

import android.support.annotation.Keep;

import com.zpf.api.IClassLoader;
@Keep
public class PluginClassLoaderImpl implements IClassLoader {
    private static class Instance {
        static PluginClassLoaderImpl loader = new PluginClassLoaderImpl();
    }

    private PluginClassLoaderImpl() {

    }

    public static PluginClassLoaderImpl get() {
        return Instance.loader;
    }

    @Override
    public Object newInstance(String name, Object... args) {
        return null;
    }

    @Override
    public Class<?> getClass(String name) {
        if ("TestMainLayout".equals(name)) {
            return TestMainLayout.class;
        }
        return null;
    }
}
