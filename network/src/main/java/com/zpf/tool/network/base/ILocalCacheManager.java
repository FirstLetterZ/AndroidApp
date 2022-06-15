package com.zpf.tool.network.base;

public interface ILocalCacheManager<R, T> {
    T searchLocal(R param);

    boolean saveToLocal(T data);
}