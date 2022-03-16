package com.zpf.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.request.IRequestCall;

import java.util.Map;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public interface IRequestFactory<T> {

    String registerCallback(@Nullable String callbackId, @NonNull IResponseListener<T> listener);

    String post(@NonNull String order, int type, @Nullable Map<String, Object> params, @Nullable String callbackId);

    String post(@NonNull String order, int type, @Nullable Map<String, Object> params, @Nullable IResponseListener<T> listener);

    boolean checkOrder(@NonNull String order, int type);

    void dispatch(@NonNull String id, @NonNull String order, int type, @Nullable Map<String, Object> params);

    IRequestCall<T> buildCall(@NonNull String order, int type, @NonNull Map<String, Object> params);

    @Nullable
    IResponse<T> findCache(String key);

    boolean updateCache(String key, T data);

    boolean cancel(String requestId);

    void cancelAll();
}