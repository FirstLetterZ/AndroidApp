package com.zpf.api;

/**
 * Created by ZPF on 2019/1/24.
 */

public interface StorageQueueInterface<T> {

    StorageQueueInterface add(T name, Object value);

    boolean commit();
}
