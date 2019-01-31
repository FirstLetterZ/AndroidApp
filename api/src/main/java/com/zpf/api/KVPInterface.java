package com.zpf.api;

/**
 * 键值对
 * Created by ZPF on 2019/1/31.
 */
public interface KVPInterface<K, V> {
    K getKey();

    V getValue();
}
