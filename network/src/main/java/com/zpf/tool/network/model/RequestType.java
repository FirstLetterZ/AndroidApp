package com.zpf.tool.network.model;

public class RequestType {
    public static final int FLAG_NULLABLE = 1;
    public static final int FLAG_NO_TOAST = 2;
    public static final int FLAG_USE_CACHE = 4;
    public static final int FLAG_UPDATE_CACHE = 8;
    public static final int FLAG_FORCE_LOAD = 16;
    public static final int FLAG_IGNORE_INTERCEPT = 32;

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_NULLABLE_NOTOAST = RequestType.FLAG_NULLABLE | RequestType.FLAG_NO_TOAST;
    public static final int TYPE_NULLABLE = RequestType.FLAG_NULLABLE;
    public static final int TYPE_NOTOAST = RequestType.FLAG_NO_TOAST;

    public static boolean checkFlag(int type, int flag) {
        return (type & flag) != 0;
    }
}
