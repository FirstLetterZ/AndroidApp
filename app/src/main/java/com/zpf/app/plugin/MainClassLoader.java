package com.zpf.app.plugin;

import android.support.annotation.Nullable;

import com.zpf.api.IClassLoader;
import com.zpf.tool.FileUtil;
import com.zpf.tool.config.AppContext;

import java.io.File;

public class MainClassLoader implements IClassLoader {

    @Override
    public Object newInstance(String name, Object... args) {
        return null;
    }

    @Override
    public Class<?> getClass(String name) {
        Class result = null;
        if ("TestMainLayout".equals(name)) {
            try {
                result = Class.forName("com.example.myplugin.TestMainLayout");
            } catch (ClassNotFoundException e) {
                //加载插件
                ClassLoader  loader = PluginUtil.getPluginClassLoader(AppContext.get(), FileUtil.getAppDataPath() + File.separator + "plugin-test.apk",
                        FileUtil.getAppCachePath(), getClass().getClassLoader());
                if (loader != null) {
                    try {
                        result = loader.loadClass("com.example.myplugin.TestMainLayout");
                    } catch (ClassNotFoundException ex) {
                        //
                    }
                }
            }
        }
        return result;
    }

    public void getClassAsync(String name, AsyncLoadListener listener) {
        Class result = null;
        ClassLoader loader = null;
        if ("TestMainLayout".equals(name)) {
            try {
                result = Class.forName("com.example.myplugin.TestMainLayout");
            } catch (ClassNotFoundException e) {
                //加载插件
                loader = PluginUtil.getPluginClassLoader(AppContext.get(), FileUtil.getAppDataPath() + File.separator + "plugin-test.apk",
                        FileUtil.getAppCachePath(), getClass().getClassLoader());
                if (loader != null) {
                    try {
                        result = loader.loadClass("com.example.myplugin.TestMainLayout");
                    } catch (ClassNotFoundException ex) {
                        //
                    }
                }
            }
        }
        if (listener != null) {
            listener.onResult(loader, result);
        }
    }


    public interface AsyncLoadListener {
        void onResult(@Nullable ClassLoader classLoader, @Nullable Class<?> result);
    }
}
