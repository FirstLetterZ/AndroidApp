package com.zpf.tool.expand.cache;

public class DataDefault {
    public static final String DEF_STRING = "";
    public static final float DEF_FLOAT = 0.0f;
    public static final boolean DEF_BOOLEAN = false;
    public static final long DEF_LONG = 0L;
    public static final int DEF_INT = 0;

    public static boolean isDefaultValue(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof Number) {
            return 0 == (int) o;
        } else if (o instanceof Boolean) {
            return DataDefault.DEF_BOOLEAN == (boolean) o;
        } else if (o instanceof String) {
            return DataDefault.DEF_STRING.equals(o);
        } else {
            return false;
        }
    }
}