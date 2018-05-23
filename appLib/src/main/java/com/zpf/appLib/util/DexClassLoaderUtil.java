package com.zpf.appLib.util;

import android.text.TextUtils;

import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.base.BaseViewLayout;
import com.zpf.appLib.constant.AppConst;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by ZPF on 2018/4/28.
 */

public class DexClassLoaderUtil {
    private HashMap<String, DexClassLoader> apkLoader = new HashMap<>();
    private HashMap<String, Class<? extends BaseViewLayout>> classMap = new HashMap<>();
    private ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
    private static volatile DexClassLoaderUtil dexClassLoaderUtil;

    public static DexClassLoaderUtil instance() {
        if (dexClassLoaderUtil == null) {
            synchronized (DexClassLoaderUtil.class) {
                if (dexClassLoaderUtil == null) {
                    dexClassLoaderUtil = new DexClassLoaderUtil();
                }
            }
        }
        return dexClassLoaderUtil;
    }

    public void jumpTo(String apkFilePath, String className, BaseViewContainer container) {
        Class<? extends BaseViewLayout> clz = classMap.get(className);
        if (clz != null) {
            container.obtainIntent(null)
                    .putExtra(AppConst.INTENT_FROM_OTHER_APK, true)
                    .putExtra(AppConst.INTENT_CLASS_NAME, className);
            container.pushActivity(clz);
        } else {
            DexClassLoader dexClassLoader = apkLoader.get(apkFilePath);
            if (dexClassLoader == null) {
                File dexOutputDir = container.getContext().getDir("dex", 0);
                final String dexOutputPath = dexOutputDir.getAbsolutePath();
                dexClassLoader = new DexClassLoader(apkFilePath,
                        dexOutputPath, null, localClassLoader);
                apkLoader.put(apkFilePath, dexClassLoader);
            }
            try {
                Class<?> targetClass = dexClassLoader.loadClass(className);
                if (targetClass != null && checkType(targetClass)) {
                    clz = (Class<? extends BaseViewLayout>) targetClass;
                    classMap.put(className, clz);
                    container.obtainIntent(null)
                            .putExtra(AppConst.INTENT_FROM_OTHER_APK, true)
                            .putExtra(AppConst.INTENT_CLASS_NAME, className);
                    container.pushActivity(clz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BaseViewLayout getViewLayout(BaseViewContainer container, String className) {
        Class<? extends BaseViewLayout> targetClass = classMap.get(className);
        if (targetClass == null) {
            return null;
        } else {
            BaseViewLayout layout = null;
            try {
                Class[] pType = new Class[]{com.zpf.appLib.base.BaseViewContainer.class};
                Constructor constructor = targetClass.getConstructor(pType);
                layout = (BaseViewLayout) constructor.newInstance(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return layout;
        }
    }

    private boolean checkType(Class<?> cls) {
        boolean result = false;
        Class<?> a = cls;
        while (a != null) {
            if (TextUtils.equals(a.getName(), BaseViewLayout.class.getName())) {
                result = true;
                break;
            } else {
                a = a.getSuperclass();
            }
        }
        return result;
    }

}
