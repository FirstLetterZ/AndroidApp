package com.zpf.support.network.base;

public interface ILocalCacheManager<T> {
    T searchLocal();

    boolean saveToLocal(T data);
}