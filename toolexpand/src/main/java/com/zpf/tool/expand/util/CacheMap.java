package com.zpf.tool.expand.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.SparseArray;

import com.zpf.api.IStorageManager;
import com.zpf.api.IStorageQueue;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.tool.config.DataDefault;
import com.zpf.tool.config.GlobalConfigImpl;

import java.util.List;

/**
 * CacheMap
 * Created by ZPF on 2018/4/16.
 */
public class CacheMap {
    public static final String CACHE_STORAGE_KEY = "cache_storage_";
    private SparseArray<Object> cacheValue = new SparseArray<>();
    private static volatile CacheMap appCacheUtil;
    private IStorageManager<String> localStorage;

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

    public synchronized static void setLocalStorage(IStorageManager<String> localStorage) {
        get().localStorage = localStorage;
    }

    public synchronized static void clear() {
        get().cacheValue.clear();
        if (get().localStorage != null) {
            get().localStorage.clearAll();
        }
    }

    public synchronized static void put(int key, Object value) {
        if (key > 0 && get().localStorage != null) {
            Object oldValue = get().cacheValue.get(key);
            get().cacheValue.put(key, value);
            if (value != oldValue) {
                get().localStorage.save(CACHE_STORAGE_KEY + key, value);
            }
        } else {
            get().cacheValue.put(key, value);
        }

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

    @Deprecated
    public synchronized static <T> T getValue(int key, @NonNull Class<T> cls) {
        Object value = null;
        try {
            value = get().cacheValue.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        T result = null;
        if (isDefaultValue(value) && get().localStorage != null) {
            try {
                value = get().localStorage.find(CACHE_STORAGE_KEY + key, cls);
            } catch (Exception e) {
                value = null;
            }
            if (value != null) {
                get().cacheValue.put(key, value);
            }
        }
        if (value != null) {
            if (value.getClass().getName().equals(cls.getName())) {
                try {
                    result = (T) value;
                } catch (Exception e) {
                    //
                }
            } else {
                JsonParserInterface jsonParser =
                        GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
                if (jsonParser != null) {
                    String valueString = jsonParser.toString(value);
                    try {
                        result = jsonParser.fromJson(valueString, cls);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }
        return result;
    }

    /**
     * 推荐使用
     */
    public synchronized static <T> T getValue(int key, @NonNull T defaultValue) {
        T result = defaultValue;
        Object value = null;
        try {
            value = get().cacheValue.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isDefaultValue(value) && get().localStorage != null) {
            try {
                value = get().localStorage.find(CACHE_STORAGE_KEY + key, defaultValue);
            } catch (Exception e) {
                value = null;
            }
            if (value != null) {
                get().cacheValue.put(key, value);
            }
        }
        if (value != null) {
            if (value.getClass().getName().equals(defaultValue.getClass().getName())) {
                try {
                    result = (T) value;
                } catch (Exception e) {
                    //
                }
            } else {
                JsonParserInterface jsonParser =
                        GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
                if (jsonParser != null) {
                    String valueString = jsonParser.toString(value);
                    try {
                        result = jsonParser.fromJson(valueString, defaultValue.getClass());
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Nullable
    public synchronized static <T> List<T> getArray(int key, @NonNull Class<T> cls) {
        Object value = get().cacheValue.get(key);
        List<T> result = null;
        if (value != null) {
            if (value instanceof List) {
                try {
                    result = (List<T>) value;
                } catch (Exception e) {
                    //
                }
            } else {
                JsonParserInterface jsonParser =
                        GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
                if (jsonParser != null) {
                    result = jsonParser.fromJsonList(value, cls);
                    if (result != null && result.size() > 0) {
                        get().cacheValue.put(key, result);
                    }
                }
            }
        } else if (get().localStorage != null) {
            try {
                value = get().localStorage.find(CACHE_STORAGE_KEY + key, cls);
            } catch (Exception e) {
                value = null;
            }
            if (value != null && value instanceof List) {
                try {
                    result = (List<T>) value;
                    if (result.size() > 0) {
                        get().cacheValue.put(key, result);
                    }
                } catch (Exception e) {
                    //
                }
            }
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

    public static CacheStorageQueue createQueue() {
        return get().startCreateQueue();
    }

    private CacheStorageQueue startCreateQueue() {
        return new CacheStorageQueue();
    }

    class CacheStorageQueue implements IStorageQueue<Integer> {
        IStorageQueue<String> localStorageQueue;

        CacheStorageQueue() {
            if (get().localStorage != null) {
                localStorageQueue = get().localStorage.createQueue();
            }
        }

        @Override
        public CacheStorageQueue add(Integer name, Object value) {
            if (name != null) {
                get().cacheValue.put(name, value);
                if (name > 0 && localStorageQueue != null) {
                    localStorageQueue.add(CACHE_STORAGE_KEY + name, value);
                }
            }
            return this;
        }

        public boolean commit() {
            return localStorageQueue != null && localStorageQueue.commit();
        }
    }
}
