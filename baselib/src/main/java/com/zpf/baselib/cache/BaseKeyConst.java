package com.zpf.baselib.cache;

/**
 * library内动态变量对应存储key值
 * key值规则：
 * 第6位及以上，代表module，第5位代表数据类型：
 * 1--BOOLEAN;2--STRING;3--INT;4--INT;5--LONG;6--FLOAT;
 * 其他值应小于10000
 * Created by ZPF on 2018/6/21.
 */

public class BaseKeyConst {
    protected final static int MODULE_BASE = 100000;
    protected final static int TYPE_BASE = 10000;
    protected final static int BOOLEAN = TYPE_BASE;
    protected final static int STRING = 2 * TYPE_BASE;
    protected final static int INT = 3 * TYPE_BASE;
    protected final static int LONG = 4 * TYPE_BASE;
    protected final static int FLOAT = 5 * TYPE_BASE;

    public static final int IS_DEBUG = MODULE_BASE + BOOLEAN + 1;
    public static final int ASSET_TOKEN = MODULE_BASE + STRING + 1;
    public static final int REFRESH_TOKEN = MODULE_BASE + STRING + 2;
    public static final int DEVICE_ID = MODULE_BASE + STRING + 3;
}
