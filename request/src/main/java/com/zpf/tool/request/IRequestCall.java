package com.zpf.tool.request;

import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.Identification;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public interface IRequestCall<T> extends ICancelable, Identification {

    IResponse<T>  execute();
    void enqueue(@Nullable IResponseListener<T> listener);
}