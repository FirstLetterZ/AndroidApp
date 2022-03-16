package com.zpf.network;

import androidx.annotation.NonNull;

/**
 * @author Created by ZPF on 2022/1/6.
 */
public interface IResponseListener<T> {

    void onStart(@NonNull String requestId);

    void onComplete(@NonNull IResponse<T> response);
}
