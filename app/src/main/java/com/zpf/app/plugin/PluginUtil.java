package com.zpf.app.plugin;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.os.Build;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginUtil {

    public static ClassLoader loadPlugin(Context context, String pluginFilePath,
                                         String optimizedDirectory, ClassLoader parentClassLoader) {
        DexClassLoader loader;
        try {
            loader = new DexClassLoader(pluginFilePath, optimizedDirectory,
                    null, parentClassLoader);
            AssetManager manager = getAssetManager(context);
            if (addAssetPath(manager, pluginFilePath) <= 0) {
                loader = null;
            }
        } catch (Exception e) {
            loader = null;
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
