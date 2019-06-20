package com.zpf.support.network.model;

import com.zpf.support.network.base.ErrorCode;
import com.zpf.support.network.base.IResponseBean;

public class ResponseResult<T> implements Cloneable, IResponseBean {
    private int code;
    private String message;
    private T data;
    private boolean success;

    public ResponseResult() {
    }

    public ResponseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean isSuccess() {
        return code == ErrorCode.RESPONSE_SUCCESS || success;
    }
}
