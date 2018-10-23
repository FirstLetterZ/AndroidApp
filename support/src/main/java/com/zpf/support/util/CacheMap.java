package com.zpf.support.util;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.zpf.generalUtil.DataDefault;

import java.util.List;

/**
 * Created by ZPF on 2018/4/16.
 */
public class CacheMap {
    public static final String CACHE_SP_KEY = "cache_map_";
    private SparseArray<Object> cacheValue = new SparseArray<>();
    private static volatile CacheMap appCacheUtil;
    private static SharedPreferences.Editor editor;

    private static CacheMap get() {
        if (appCacheUtil == null) {
            synchronized (CacheMap.class) {
                if (appCacheUtil == null) {
                    appCacheUtil = new CacheMap();
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
            SpUtil.putValue(CACHE_SP_KEY + key, value);
        }
    }

    public interface Editor {
        Editor putValues(int key, Object value);

        boolean commit();
    }

    public static Editor edit() {
        return new Editor() {

            @Override
            public Editor putValues(int key, Object value) {
                if (value == null) {
                    get().cacheValue.remove(key);
                } else {
                    get().cacheValue.put(key, value);
                }
                if (key > 0) {
                    editor = SpUtil.putValues(editor, CACHE_SP_KEY + key, value);
                }
                return this;
            }

            @Override
            public boolean commit() {
                boolean result = false;
                if (editor != null) {
                    result = editor.commit();
                    editor = null;
                }
                return result;
            }
        };
    }

    public static String getString(int key) {
        return getValue(key, DataDefault.DEF_STRING);
    }

    public static float getFloat(int key) {
        return getValue(key, DataDefault.DEF_FLOAT);
    }

    public static boolean getBoolean(int key) {
        return getValue(key, DataDefault.DEF_BOOLEAN);
    }

    public static long getLong(int key) {
        return getValue(key, DataDefault.DEF_LONG);
    }

    public static int getInt(int key) {
        return getValue(key, DataDefault.DEF_INT);
    }

    /**
     * 不推荐使用
     */
    @Deprecated
    public static <T> T getValue(int key, @NonNull Class<T> cls) {
        Object value = get().cacheValue.get(key);
        T result = null;
        if (isDefaultValue(value)) {
            value = SpUtil.getValue(CACHE_SP_KEY + key, cls);
            if (value != null) {
                get().cacheValue.put(key, value);
            }
        }
        if (value != null) {
            if (value.getClass().getName().equals(cls.getName())) {
                try {
                    result = (T) value;
                } catch (Exception e) {
                    LogUtil.w("CacheMap getValue fail:" + e.toString());
                }
            } else {
                String valueString = JsonUtil.toString(value);
                try {
                    result = JsonUtil.fromJson(valueString, cls);
                } catch (Exception e) {
                    LogUtil.w("CacheMap getValue fail:" + e.toString());
                }
            }
        }
        return result;
    }

    @Nullable
    public static <T> List<T> getArray(int key, @NonNull Class<T> cls) {
        Object value = get().cacheValue.get(key);
        List<T> result = null;
        if (value != null) {
            if (value instanceof List) {
                try {
                    result = (List<T>) value;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (value instanceof String && !TextUtils.isEmpty((String) value)) {
                result = JsonUtil.fromJsonList((String) value, cls);
                get().cacheValue.put(key, result);
            } else {
                value = null;
            }
        }
        if (value == null) {
            String jsonString = SpUtil.getString(CACHE_SP_KEY + key);
            if (!TextUtils.isEmpty(jsonString)) {
                result = JsonUtil.fromJsonList(jsonString, cls);
                get().cacheValue.put(key, result);
            } else {
                get().cacheValue.remove(key);
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
        if (isDefaultValue(value)) {
            value = SpUtil.getValue(CACHE_SP_KEY + key, defaultValue);
            if (value != null) {
                get().cacheValue.put(key, value);
            }
        }
        if (value != null) {
            if (value.getClass().getName().equals(defaultValue.getClass().getName())) {
                try {
                    result = (T) value;
                } catch (Exception e) {
                    LogUtil.w("CacheMap getValue fail:" + e.toString());
                }
            } else {
                String valueString = JsonUtil.toString(value);
                try {
                    result = (T) JsonUtil.fromJson(valueString, defaultValue.getClass());
                } catch (Exception e) {
                    LogUtil.w("CacheMap getValue fail:" + e.toString());
                }
            }
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    private static boolean isDefaultValue(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof String) {
            return DataDefault.DEF_STRING.equals(o);
        } else if (o instanceof Integer) {
            return DataDefault.DEF_INT == (int) o;
        } else if (o instanceof Double) {
            return DataDefault.DEF_DOUBLE == (double) o;
        } else if (o instanceof Long) {
            return DataDefault.DEF_LONG == (long) o;
        } else if (o instanceof Float) {
            return DataDefault.DEF_FLOAT == (float) o;
        } else {
            return o instanceof Boolean && DataDefault.DEF_BOOLEAN == (boolean) o;
        }
    }

}
