package com.zpf.appLib.util;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.zpf.appLib.constant.AppConst;

/**
 * Created by ZPF on 2018/4/16.
 */

public class CacheMapUtil {
    private SparseArray<Object> cacheValue = new SparseArray<>();
    private static volatile CacheMapUtil appCacheUtil;

    public static CacheMapUtil get() {
        if (appCacheUtil == null) {
            synchronized (CacheMapUtil.class) {
                if (appCacheUtil == null) {
                    appCacheUtil = new CacheMapUtil();
                }
            }
        }
        return appCacheUtil;
    }


    public static void clearCache() {
        get().cacheValue.clear();
    }

    public static void putValue(int key, Object value) {
        if (value == null) {
            get().cacheValue.remove(key);
        } else {
            get().cacheValue.put(key, value);
        }
        if (key > 0) {
            SpUtil.putValue(AppConst.CACHE_SP_KEY + key, value);
        }
    }

    public static String getString(int key) {
        return getValue(key, AppConst.DEF_STRING);
    }

    public static float getFloat(int key) {
        return getValue(key, AppConst.DEF_FLOAT);
    }

    public static boolean getBoolean(int key) {
        return getValue(key, AppConst.DEF_BOOLEAN);
    }

    public static long getLong(int key) {
        return getValue(key, AppConst.DEF_LONG);
    }

    public static int getInt(int key) {
        return getValue(key, AppConst.DEF_INT);
    }

    /**
     * 不推荐使用
     */
    @Deprecated
    public static <T> T getValue(int key, @NonNull Class<T> cls) {
        Object value = get().cacheValue.get(key);
        T result = null;
        if (value == null) {
            value = SpUtil.getValue(AppConst.CACHE_SP_KEY + key, cls);
            if (value != null) {
                get().cacheValue.put(key, value);
            }
        }
        if (value != null) {
            if (value.getClass().getName().equals(cls.getName())) {
                try {
                    result = (T) value;
                } catch (Exception e) {
                    LogUtil.w("CacheMapUtil getValue fail:" + e.toString());
                }
            } else {
                String valueString = JsonUtil.toString(value);
                try {
                    result = JsonUtil.fromJson(valueString, cls);
                } catch (Exception e) {
                    LogUtil.w("CacheMapUtil getValue fail:" + e.toString());
                }
            }
        }
        return result;
    }

    /**
     * 推荐使用
     */
    public static <T> T getValue(int key, @NonNull T defaultValue) {
        T result = defaultValue;
        Object value = get().cacheValue.get(key);
        if (value == null) {
            value = SpUtil.getValue(AppConst.CACHE_SP_KEY + key, defaultValue);
            if (value != null) {
                get().cacheValue.put(key, value);
            }
        }
        if (value != null) {
            if (value.getClass().getName().equals(defaultValue.getClass().getName())) {
                try {
                    result = (T) value;
                } catch (Exception e) {
                    LogUtil.w("CacheMapUtil getValue fail:" + e.toString());
                }
            } else {
                String valueString = JsonUtil.toString(value);
                try {
                    result = (T) JsonUtil.fromJson(valueString, defaultValue.getClass());
                } catch (Exception e) {
                    LogUtil.w("CacheMapUtil getValue fail:" + e.toString());
                }
            }
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

}
