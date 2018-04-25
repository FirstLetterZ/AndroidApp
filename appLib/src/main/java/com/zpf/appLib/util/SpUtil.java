package com.zpf.appLib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.zpf.appLib.constant.AppConst;

/**
 * Created by ZPF on 2017/9/29.
 */
public class SpUtil {

    private static volatile SpUtil mySpUtil;
    private SharedPreferences sp;

    private SpUtil() {
        sp = AppConst.instance().getApplication()
                .getSharedPreferences(AppConst.CACHE_SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    private static SpUtil get() {
        if (mySpUtil == null) {
            synchronized (SpUtil.class) {
                if (mySpUtil == null) {
                    mySpUtil = new SpUtil();
                }
            }
        }
        return mySpUtil;
    }

    public static void putValue(String key, Object value) {
        SharedPreferences.Editor editor = get().sp.edit();
        if (key == null) {
            return;
        } else if (value == null) {
            editor.remove(key);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (int) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (long) value);
        } else {
            editor.putString(key, JsonUtil.toString(value));
        }
        editor.commit();
    }

    public static <T> T getValue(@NonNull String key, @NonNull Class<T> cls) {
        T result = null;
        if (cls.getName().equals(String.class.getName())) {
            result = (T) getString(key);
        } else if (cls.getName().equals(Integer.class.getName())) {
            result = (T) (Integer) getInt(key);
        } else if (cls.getName().equals(Long.class.getName())) {
            result = (T) ((Long) getLong(key));
        } else if (cls.getName().equals(Float.class.getName())) {
            result = (T) ((Float) getFloat(key));
        } else if (cls.getName().equals(Boolean.class.getName())) {
            result = (T) ((Boolean) getBoolean(key));
        } else {
            String str = getString(key);
            if (str.length() > 0) {
                result = JsonUtil.fromJson(str, cls);
            }
        }
        return result;
    }

    public static String getString(@NonNull String key) {
        return get().sp.getString(key, AppConst.DEF_STRING);
    }


    public static boolean getBoolean(@NonNull String key) {
        return get().sp.getBoolean(key, AppConst.DEF_BOOLEAN);
    }

    public static int getInt(@NonNull String key) {
        return get().sp.getInt(key, AppConst.DEF_INT);
    }


    public static long getLong(@NonNull String key) {
        return get().sp.getLong(key, AppConst.DEF_LONG);
    }

    public static float getFloat(@NonNull String key) {
        return get().sp.getFloat(key, AppConst.DEF_FLOAT);
    }

    public static <T> T getValue(@NonNull String key, @NonNull T defaultValue) {
        if (defaultValue instanceof String) {
            defaultValue = (T) getString(key);
        } else if (defaultValue instanceof Integer) {
            defaultValue = (T) (Integer) getInt(key);
        } else if (defaultValue instanceof Long) {
            defaultValue = (T) (Long) getLong(key);
        } else if (defaultValue instanceof Float) {
            defaultValue = (T) (Float) getFloat(key);
        } else if (defaultValue instanceof Boolean) {
            defaultValue = (T) (Boolean) getBoolean(key);
        } else {
            String str = getString(key);
            if (str.length() > 0) {
                try {
                    defaultValue = (T) JsonUtil.fromJson(str, defaultValue.getClass());
                } catch (Exception e) {
                    LogUtil.w("SpUtil getValue fail:" + e.toString());
                }
            }
        }
        return defaultValue;
    }

    public static void remove(String key) {
        get().sp.edit().remove(key).commit();
    }

    public static void clear() {
        get().sp.edit().clear().commit();
    }

    public interface ContextHelper {
        Context getContext();
    }

}
