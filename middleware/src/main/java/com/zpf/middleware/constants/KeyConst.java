package com.zpf.middleware.constants;

/**
 * 全局动态变量对应存储key值
 * key值规则：
 * 0<key<2000--int;
 * 2000<key<3000--float;
 * 3000<key<4000--long;
 * 4000<key<5000--boolean;
 * key>5000--String
 * 对应的负数范围类型相同，不进入SP保存
 * Created by ZPF on 2018/4/23.
 */

public class KeyConst {
    /*==================== int 0<key<2000=======================*/

    public static final int SCREEN_HEIGHT = 1;
    public static final int SCREEN_WIDTH = 2;
    public static final int SCREEN_STATUS_BAR = 3;

    /*==================== float 2000<key<3000=======================*/

    public static final int SCREEN_DENSITY = 2001;

   /*==================== boolean 4000<key<5000=======================*/


    /*==================== String key>5000=======================*/

    public static final int ACCESS_TOKEN = 5001;


}
