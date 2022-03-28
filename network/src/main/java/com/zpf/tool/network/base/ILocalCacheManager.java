package com.zpf.tool.network.base;

public interface ILocalCacheManager<T> {
    T searchLocal();

    boolean saveToLocal(T data);
}