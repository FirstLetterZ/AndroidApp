package com.zpf.tool.expand.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.zpf.api.IStorageManager;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.api.IStorageQueue;
import com.zpf.tool.config.AppContext;
import com.zpf.tool.config.DataDefault;
import com.zpf.tool.config.GlobalConfigImpl;

/**
 * Created by ZPF on 2017/9/29.
 */
public class SpUtil implements IStorageManager<String> {
    private static volatile SpUtil mySpUtil;
    private SharedPreferences sp;
    public static final String SP_FILE_NAME = "sp_data_file";

    private SpUtil() {

    }

    public static SpUtil get() {
        if (mySpUtil == null) {
            synchronized (SpUtil.class) {
                if (mySpUtil == null) {
                    mySpUtil = new SpUtil();
                }
            }
        }
        return mySpUtil;
    }

    public static void put(String key, Object value) {
        SharedPreferences.Editor editor = getSP().edit();
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
            JsonParserInterface jsonParser =
                    GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
            if (jsonParser != null) {
                editor.putString(key, jsonParser.toString(value));
            }
        }
        editor.commit();
    }

    public static String getString(@NonNull String key) {
        return getSP().getString(key, DataDefault.DEF_STRING);
    }

    public static boolean getBoolean(@NonNull String key) {
        return getSP().getBoolean(key, DataDefault.DEF_BOOLEAN);
    }

    public static int getInt(@NonNull String key) {
        return getSP().getInt(key, DataDefault.DEF_INT);
    }


    public static long getLong(@NonNull String key) {
        return getSP().getLong(key, DataDefault.DEF_LONG);
    }

    public static float getFloat(@NonNull String key) {
        return getSP().getFloat(key, DataDefault.DEF_FLOAT);
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
            JsonParserInterface jsonParser =
                    GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
            if (jsonParser != null) {
                String str = getString(key);
                if (str.length() > 0) {
                    result = jsonParser.fromJson(str, cls);
                }
            }
        }
        return result;
    }

    public static <T> T getValue(@NonNull String key, @NonNull T defaultValue) {
        if (defaultValue instanceof String) {
            defaultValue = (T) get().getSP().getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            defaultValue = (T) (Integer) get().getSP().getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            defaultValue = (T) (Long) get().getSP().getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            defaultValue = (T) (Float) get().getSP().getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            defaultValue = (T) (Boolean) get().getSP().getBoolean(key, (Boolean) defaultValue);
        } else {
            JsonParserInterface jsonParser =
                    GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
            if (jsonParser != null) {
                String str = getString(key);
                if (str.length() > 0) {
                    defaultValue = jsonParser.fromJson(str, defaultValue.getClass());
                }
            }
        }
        return defaultValue;
    }

    public static void remove(String key) {
        getSP().edit().remove(key).commit();
    }

    public static void clear() {
        getSP().edit().clear().commit();
    }

    private static SharedPreferences getSP() {
        if (get().sp == null) {
            get().sp = AppContext.get().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        }
        return get().sp;
    }

    public static SharedPreferences.Editor add(SharedPreferences.Editor editor, String key, Object value) {
        if (editor == null) {
            editor = getSP().edit();
        }
        if (key == null) {
            return editor;
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
            JsonParserInterface jsonParser =
                    GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
            if (jsonParser != null) {
                editor.putString(key, jsonParser.toString(value));
            }
        }
        return editor;
    }

    @Override
    public SpUtil save(String name, Object value) {
        put(name, value);
        return this;
    }

    @Override
    public <T> T find(String key, @NonNull Class<T> cls) {
        return getValue(key, cls);
    }

    @Override
    public <T> T find(String key, @NonNull T defValue) {
        return getValue(key, defValue);
    }

    @Override
    public void clearAll() {
        clear();
    }

    @Override
    public IStorageQueue<String> createQueue() {
        return new SpStorageQueue();
    }

    class SpStorageQueue implements IStorageQueue<String> {
        SharedPreferences.Editor editor;

        public SpStorageQueue() {
            editor = getSP().edit();
        }

        @Override
        public SpStorageQueue add(String name, Object value) {
            editor = SpUtil.add(editor, name, value);
            return this;
        }

        public boolean commit() {
            return editor.commit();
        }
    }
}
