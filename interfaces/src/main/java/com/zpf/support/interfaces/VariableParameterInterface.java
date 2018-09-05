package com.zpf.support.interfaces;

/**
 * 用于标记变量，每次使用的时候应通过getCurrentValue获取当前值
 * Created by ZPF on 2018/9/5.
 */
public interface VariableParameterInterface {
    Object getCurrentValue();
}
