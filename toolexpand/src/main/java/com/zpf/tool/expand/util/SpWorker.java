package com.zpf.tool.expand.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.tool.config.AppContext;
import com.zpf.tool.config.DataDefault;
import com.zpf.tool.config.GlobalConfigImpl;

public class SpWorker {
    private SharedPreferences mSP;

    public SpWorker(String name) {
        mSP = AppContext.get().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void put(String key, Object value) {
        SharedPreferences.Editor editor = mSP.edit();
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

    public <T> T getValue(@NonNull String key, @NonNull T defaultValue) {
        if (defaultValue instanceof String) {
            defaultValue = (T) mSP.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            defaultValue = (T) (Integer) mSP.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            defaultValue = (T) (Long) mSP.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            defaultValue = (T) (Float) mSP.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            defaultValue = (T) (Boolean) mSP.getBoolean(key, (Boolean) defaultValue);
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

    public void remove(String key) {
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
            JsonParserInterface jsonParser =
                    GlobalConfigImpl.get().getGlobalInstance(JsonParserInterface.class);
            if (jsonParser != null) {
                editor.putString(key, jsonParser.toString(value));
            }
        }
        return editor;
    }

    public SharedPreferences getSharedPreferences() {
        return mSP;
    }

}
