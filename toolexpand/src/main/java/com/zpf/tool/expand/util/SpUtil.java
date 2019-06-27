package com.zpf.tool.expand.util;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.zpf.api.IStorageManager;
import com.zpf.api.IStorageQueue;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZPF on 2017/9/29.
 */
public class SpUtil implements IStorageManager<String> {
    private SpWorker defWorker;
    private ConcurrentHashMap<String, SpWorker> spMap;
    public static final String SP_FILE_NAME = "sp_data_file";

    private static class Instance {
        static final SpUtil mInstance = new SpUtil();
    }

    private SpUtil() {
        spMap = new ConcurrentHashMap<>();
        defWorker = new SpWorker(SP_FILE_NAME);
        spMap.put(SP_FILE_NAME, defWorker);
    }

    public static SpUtil get() {
        return Instance.mInstance;
    }

    public static SpWorker get(String fileName) {
        SpWorker worker = Instance.mInstance.spMap.get(fileName);
        if (worker == null) {
            worker = new SpWorker(fileName);
            Instance.mInstance.spMap.put(fileName, worker);
        }
        return worker;
    }

    public static void put(String key, Object value) {
        Instance.mInstance.defWorker.put(key, value);
    }

    public static String getString(@NonNull String key) {
        return Instance.mInstance.defWorker.getString(key);
    }

    public static boolean getBoolean(@NonNull String key) {
        return Instance.mInstance.defWorker.getBoolean(key);
    }

    public static int getInt(@NonNull String key) {
        return Instance.mInstance.defWorker.getInt(key);
    }


    public static long getLong(@NonNull String key) {
        return Instance.mInstance.defWorker.getLong(key);
    }

    public static float getFloat(@NonNull String key) {
        return Instance.mInstance.defWorker.getFloat(key);
    }

    public static <T> T getValue(@NonNull String key, @NonNull Class<T> cls) {
        return Instance.mInstance.defWorker.getValue(key, cls);
    }

    public static <T> T getValue(@NonNull String key, @NonNull T defaultValue) {
        return Instance.mInstance.defWorker.getValue(key, defaultValue);
    }

    public static void remove(String key) {
        Instance.mInstance.defWorker.remove(key);
    }

    public static void clear() {
        Instance.mInstance.defWorker.clear();
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
            editor = defWorker.getSharedPreferences().edit();
        }

        @Override
        public SpStorageQueue add(String name, Object value) {
            editor = defWorker.add(editor, name, value);
            return this;
        }

        public boolean commit() {
            return editor.commit();
        }
    }

}
