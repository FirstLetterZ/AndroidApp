package com.zpf.tool.expand;

import android.support.annotation.IntRange;

/**
 * Created by ZPF on 2019/1/23.
 */
public class CacheKey {
    protected final static int MODULE_BASE = 100000;
    protected final static int TYPE_BASE = 10000;
    protected final static int INT = 2 * TYPE_BASE;
    protected final static int BOOLEAN = 3 * TYPE_BASE;
    protected final static int LONG = 4 * TYPE_BASE;
    protected final static int FLOAT = 5 * TYPE_BASE;
    protected final static int STRING = 6 * TYPE_BASE;

    public static int getBaseIntKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE + INT + key;
    }

    public static int getModuleIntKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE * moduleId() + INT + key;
    }

    public static int getBaseBooleanKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE + BOOLEAN + key;
    }

    public static int getModuleBooleanKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE * moduleId() + BOOLEAN + key;
    }

    public static int getBaseStringKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE + STRING + key;
    }

    public static int getModuleStringKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE * moduleId() + STRING + key;
    }

    public static int getBaseLongKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE + LONG + key;
    }

    public static int getModuleLongKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE * moduleId() + LONG + key;
    }

    public static int getBaseFloatKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE + FLOAT + key;
    }

    public static int getModuleFloatKey(@IntRange(from = 1, to = TYPE_BASE) int key) {
        return MODULE_BASE * moduleId() + FLOAT + key;
    }

    private static int moduleId() {
        int id = getModuleId();
        if (id < 1) {
            id = 1;
        } else if (id > 21473) {
            id = 21473;
        }
        return id;
    }

    protected static int getModuleId() {
        return 1;
    }
}