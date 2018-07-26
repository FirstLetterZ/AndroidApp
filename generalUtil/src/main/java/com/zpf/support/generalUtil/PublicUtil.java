package com.zpf.support.generalUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ZPF on 2018/7/26.
 */

public class PublicUtil {

    public static int getColor(int color) {
        return AppContext.get().getResources().getColor(color);
    }

    public static String getString(int id) {
        return AppContext.get().getResources().getString(id);
    }

    public static <T> Class<T> getViewClass(Class<?> klass) {
        Type type = klass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) return null;
        return (Class<T>) types[0];
    }

    public static int getVersionCode(Context context) {
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        String versionName = "0.0.1";
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                versionName = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    public static String getAppName(Context context) {
        String name = "当前应用";
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                name = info.packageName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }
}
