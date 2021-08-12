package com.zpf.tool.expand.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IStorageChangedListener;
import com.zpf.api.IStorageManager;
import com.zpf.api.IStorageQueue;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.tool.global.CentralManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CacheMap
 * Created by ZPF on 2018/4/16.
 */
public class CacheMap {
    public static final String CACHE_STORAGE_KEY = "cache_storage_";
    private static final ConcurrentHashMap<String, Object> cacheValue = new ConcurrentHashMap<>();
    private static IStorageManager<String> localStorageManager;
    private static IStorageChangedListener<Integer> changedListener;

    public synchronized static void setLocalStorageManager(IStorageManager<String> manager) {
        localStorageManager = manager;
    }

    public static void setCacheChangedListener(IStorageChangedListener<Integer> listener) {
        changedListener = listener;
    }

    public synchronized static void clear() {
        cacheValue.clear();
        if (localStorageManager != null) {
            localStorageManager.clearAll();
        }
        if (changedListener != null) {
            changedListener.onStorageClear();
        }
    }

    public static void put(int key, Object value) {
        if (key > 0 && localStorageManager != null) {
            Object oldValue = cacheValue.get(CACHE_STORAGE_KEY + key);
            putValue(key, value);
            if (value != oldValue || (value != null && !value.equals(oldValue))) {
                localStorageManager.save(CACHE_STORAGE_KEY + key, value);
            }
        } else {
            putValue(key, value);
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

    @Nullable
    public synchronized static <T> T getValue(int key, @NonNull Class<T> cls) {
        Object value = null;
        try {
            value = cacheValue.get(CACHE_STORAGE_KEY + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        T result = null;
        if (DataDefault.isDefaultValue(value) && localStorageManager != null) {
            try {
                value = localStorageManager.find(CACHE_STORAGE_KEY + key, cls);
            } catch (Exception e) {
                value = null;
            }
            if (value != null) {
                putValue(key, value);
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
                JsonParserInterface jsonParser = CentralManager.getInstance(JsonParserInterface.class);
                if (jsonParser != null) {
                    try {
                        result = jsonParser.fromJson(value, cls);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }
        return result;
    }

    @NonNull
    public synchronized static <T> T getValue(int key, @NonNull T defaultValue) {
        T result = defaultValue;
        Object value = null;
        try {
            value = cacheValue.get(CACHE_STORAGE_KEY + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DataDefault.isDefaultValue(value) && localStorageManager != null) {
            try {
                value = localStorageManager.find(CACHE_STORAGE_KEY + key, defaultValue);
            } catch (Exception e) {
                value = null;
            }
            if (value != null) {
                putValue(key, value);
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
                JsonParserInterface jsonParser = CentralManager.getInstance(JsonParserInterface.class);
                if (jsonParser != null) {
                    try {
                        result = jsonParser.fromJson(value, defaultValue.getClass());
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
        Object value = cacheValue.get(CACHE_STORAGE_KEY + key);
        List<T> result = null;
        if (value != null) {
            if (value instanceof List) {
                try {
                    result = (List<T>) value;
                } catch (Exception e) {
                    //
                }
            } else {
                JsonParserInterface jsonParser = CentralManager.getInstance(JsonParserInterface.class);
                if (jsonParser != null) {
                    result = jsonParser.fromJsonList(value, cls);
                    if (result != null && result.size() > 0) {
                        putValue(key, result);
                    }
                }
            }
        } else if (localStorageManager != null) {
            try {
                value = localStorageManager.find(CACHE_STORAGE_KEY + key, cls);
            } catch (Exception e) {
                //
            }
            if (value instanceof List) {
                try {
                    result = (List<T>) value;
                    if (result.size() > 0) {
                        putValue(key, result);
                    }
                } catch (Exception e) {
                    //
                }
            }
        }
        return result;
    }

    public static IStorageQueue<Integer> createQueue() {
        return new CacheStorageQueue();
    }

    private static void putValue(int key, Object value) {
        cacheValue.put(CACHE_STORAGE_KEY + key, value);
        if (changedListener != null) {
            changedListener.onStorageChanged(key, value);
        }
    }

    static class CacheStorageQueue implements IStorageQueue<Integer> {
        IStorageQueue<String> localStorageQueue;

        CacheStorageQueue() {
            if (localStorageManager != null) {
                localStorageQueue = localStorageManager.createQueue();
            }
        }

        @Override
        public CacheStorageQueue add(Integer name, Object value) {
            if (name != null) {
                putValue(name, value);
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
