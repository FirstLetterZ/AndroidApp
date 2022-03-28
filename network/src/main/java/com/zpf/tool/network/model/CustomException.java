package com.zpf.tool.network.model;

/**
 * Created by ZPF on 2018/9/18.
 */
public class CustomException extends Exception {
    private int code;

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
