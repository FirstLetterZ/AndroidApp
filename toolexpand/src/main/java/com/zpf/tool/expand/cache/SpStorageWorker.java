package com.zpf.tool.expand.cache;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.zpf.api.IStorageManager;
import com.zpf.api.IStorageQueue;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.tool.config.AppContext;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.SpInstance;

public class SpStorageWorker implements IStorageManager<String> {
    private final SharedPreferences mSP;
    private JsonParserInterface jsonParser;

    public SpStorageWorker() {
        mSP = SpInstance.get();
    }

    public SpStorageWorker(SharedPreferences sharedPreferences) {
        mSP = sharedPreferences;
    }

    public SpStorageWorker(String name) {
        mSP = AppContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void put(@NonNull String key, Object value) {
        if (key == null) {
            return;
        }
        SharedPreferences.Editor editor = mSP.edit();
        if (value == null) {
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
            String cacheString = putCacheObject(key, value);
            if (cacheString != null) {
                editor.putString(key, cacheString);
            }
        }
        editor.commit();
    }

    public String getString(@NonNull String key) {
        return mSP.getString(key, DataDefault.DEF_STRING);
    }

    public boolean getBoolean(@NonNull String key) {
        return mSP.getBoolean(key, DataDefault.DEF_BOOLEAN);
    }

    public int getInt(@NonNull String key) {
        return mSP.getInt(key, DataDefault.DEF_INT);
    }


    public long getLong(@NonNull String key) {
        return mSP.getLong(key, DataDefault.DEF_LONG);
    }

    public float getFloat(@NonNull String key) {
        return mSP.getFloat(key, DataDefault.DEF_FLOAT);
    }

    public <T> T getValue(@NonNull String key, @NonNull Class<T> cls) {
        T result;
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
            result = getCacheObject(key, cls);
        }
        return result;
    }

    public <T> T getValue(@NonNull String key, @NonNull T defaultValue) {
        T result;
        if (defaultValue instanceof String) {
            result = (T) mSP.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            result = (T) (Integer) mSP.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            result = (T) (Long) mSP.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            result = (T) (Float) mSP.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            result = (T) (Boolean) mSP.getBoolean(key, (Boolean) defaultValue);
        } else {
            result = (T) getCacheObject(key, defaultValue.getClass());
        }
        if (result != null) {
            return result;
        } else {
            return defaultValue;
        }
    }

    public void remove(@NonNull String key) {
        if (key == null) {
            return;
        }
        mSP.edit().remove(key).commit();
    }

    public void clear() {
        mSP.edit().clear().commit();
    }

    public SharedPreferences.Editor add(SharedPreferences.Editor editor, String key, Object value) {
        if (editor == null) {
            editor = mSP.edit();
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
            String cacheString = putCacheObject(key, value);
            if (cacheString != null) {
                editor.putString(key, cacheString);
            }
        }
        return editor;
    }

    public SharedPreferences getSharedPreferences() {
        return mSP;
    }

    public void setJsonParser(JsonParserInterface parser) {
        jsonParser = parser;
    }

    protected JsonParserInterface getJsonParser() {
        if (jsonParser == null) {
            jsonParser = GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
        }
        return jsonParser;
    }

    protected String putCacheObject(@NonNull String key, Object value) {
        JsonParserInterface jsonParser = getJsonParser();
        if (jsonParser != null) {
            return jsonParser.toString(value);
        }
        return null;
    }

    protected <T> T getCacheObject(@NonNull String key, @NonNull Class<T> cls) {
        JsonParserInterface jsonParser = getJsonParser();
        if (jsonParser != null) {
            String str = getString(key);
            if (str.length() > 0) {
                return jsonParser.fromJson(str, cls);
            }
        }
        return null;
    }

    @Override
    public IStorageManager<String> save(String name, Object value) {
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
        return new SpStorageQueue(this);
    }
}
