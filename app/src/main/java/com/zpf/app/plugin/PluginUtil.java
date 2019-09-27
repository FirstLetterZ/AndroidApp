package com.zpf.app.plugin;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.ArrayMap;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginUtil {
    private static ArrayMap<String, DexClassLoader> classLoaderMap = new ArrayMap<>();

    public static ClassLoader getPluginClassLoader(Context context, String pluginFilePath,
                                                   String optimizedDirectory, ClassLoader parentClassLoader) {
        DexClassLoader loader = null;
        File pluginFile = null;
        if (pluginFilePath != null && pluginFilePath.length() > 0) {
            pluginFile = new File(pluginFilePath);
        }
        if (pluginFile != null && pluginFile.exists()) {
            loader = classLoaderMap.get(pluginFile.getAbsolutePath());
            if (loader == null) {
                try {
                    loader = new DexClassLoader(pluginFile.getAbsolutePath(), optimizedDirectory,
                            null, parentClassLoader);
                    AssetManager manager = getAssetManager(context);
                    if (addAssetPath(manager, pluginFile.getAbsolutePath()) > 0) {
                        classLoaderMap.put(pluginFile.getAbsolutePath(), loader);
                    } else {
                        loader = null;
                    }
                } catch (Exception e) {
                    loader = null;
                }
            }
        }
        return loader;
    }

    public static AssetManager getAssetManager(Context context) {
        Context nextContext;
        while ((context instanceof ContextWrapper) &&
                (nextContext = ((ContextWrapper) context).getBaseContext()) != null) {
            context = nextContext;
        }
        AssetManager assetManager;
        try {
            Method method = context.getClass().getDeclaredMethod("getAssets");
            assetManager = (AssetManager) method.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
            assetManager = null;
        }
        return assetManager;
    }

    public static int addAssetPath(AssetManager assetManager, String filePath) {
        int index = -1;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                index = (int) AssetManager.class.getMethod("addAssetPathInternal", String.class, boolean.class, boolean.class)
                        .invoke(assetManager, filePath, false, false);
            } else {
                index = (int) AssetManager.class.getDeclaredMethod("addAssetPath", String.class)
                        .invoke(assetManager, filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}
