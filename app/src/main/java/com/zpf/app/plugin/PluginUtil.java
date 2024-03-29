package com.zpf.app.plugin;

import android.annotation.SuppressLint;
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
        AssetManager assetManager = context.getAssets();
        if (assetManager != null) {
            return assetManager;
        }
        try {
            Method method = context.getClass().getDeclaredMethod("getAssets");
            assetManager = (AssetManager) method.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
            assetManager = null;
        }
        return assetManager;
    }

    @SuppressLint("SoonBlockedPrivateApi")
    public static int addAssetPath(AssetManager assetManager, String filePath) {
        int index = -1;
        Object result;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                result = AssetManager.class.getDeclaredMethod("addAssetPathInternal",
                        String.class, boolean.class, boolean.class)
                        .invoke(assetManager, filePath, false, false);
            } else {
                result = AssetManager.class.getDeclaredMethod("addAssetPath", String.class)
                        .invoke(assetManager, filePath);
            }
            index = (int) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}
