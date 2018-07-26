package com.zpf.support.constant;

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
    /*======================================= int =======================================*/
    public static final int SCREEN_HEIGHT = MODULE_BASE + INT + 1;
    public static final int SCREEN_WIDTH = MODULE_BASE + INT + 2;
    public static final int SCREEN_STATUS_BAR = MODULE_BASE + INT + 3;

    /*======================================= float =======================================*/
    public static final int SCREEN_DENSITY = MODULE_BASE + FLOAT + 1;

    /*======================================= long =======================================*/

    /*======================================= boolean =======================================*/
    public static final int IS_DEBUG = MODULE_BASE + BOOLEAN + 1;
    public static final int IS_LOGIN_SUCCESS = MODULE_BASE + BOOLEAN + 2;
    public static final int IS_NEED_UPDATE = MODULE_BASE + BOOLEAN + 3;

    /*======================================= string =======================================*/
    public static final int DEVICE_INFO = MODULE_BASE + STRING + 1;
    public static final int TOKEN_INFO = MODULE_BASE + STRING + 2;
    public static final int USER_INFO = MODULE_BASE + STRING + 3;
    public static final int PUSH_INFO = MODULE_BASE + STRING + 4;
    public static final int UPDATE_INFO = MODULE_BASE + STRING + 5;
    public static final int HOST_INFO = MODULE_BASE + STRING + 6;
    public static final int VERSION_INFO = MODULE_BASE + INT + 7;

}
