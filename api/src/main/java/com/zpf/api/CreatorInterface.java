package com.zpf.api;

/**
 * Created by ZPF on 2018/12/12.
 */
public interface CreatorInterface<T> {
    T create(int id, Object... params);
}
