package com.zpf.tool.expand.cache;

import androidx.annotation.NonNull;

import com.zpf.tool.config.SpInstance;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ZPF on 2017/9/29.
 */
public class SpUtil {
    private final SpStorageWorker defWorker;
    private final ConcurrentHashMap<String, SpStorageWorker> spMap;

    private static class Instance {
        static final SpUtil mInstance = new SpUtil();
    }

    private SpUtil() {
        spMap = new ConcurrentHashMap<>();
        defWorker = new SpStorageWorker();
        spMap.put(SpInstance.SP_FILE_NAME, defWorker);
    }

    public static SpStorageWorker get() {
        return Instance.mInstance.defWorker;
    }

    public static SpStorageWorker get(String fileName) {
        SpStorageWorker worker = Instance.mInstance.spMap.get(fileName);
        if (worker == null) {
            worker = new SpStorageWorker(fileName);
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

    public static void clearInstanceCache() {
        Instance.mInstance.spMap.clear();
    }

}