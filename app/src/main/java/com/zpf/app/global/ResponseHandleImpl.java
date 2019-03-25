package com.zpf.app.global;

import com.zpf.support.network.base.ResponseHandleInterface;
import com.zpf.support.network.model.HttpResult;
import com.zpf.tool.ToastUtil;

/**
 * Created by ZPF on 2019/3/25.
 */

public class ResponseHandleImpl implements ResponseHandleInterface {
    private static volatile ResponseHandleImpl mInstance;

    private ResponseHandleImpl() {
    }

    public static ResponseHandleImpl get() {
        if (mInstance == null) {
            synchronized (ResponseHandleImpl.class) {
                if (mInstance == null) {
                    mInstance = new ResponseHandleImpl();
                }
            }
        }
        return mInstance;
    }

    @Override
    public HttpResult parsingException(Throwable e) {
        return null;
    }

    @Override
    public boolean checkDataNull(Object data) {
        return false;
    }

    @Override
    public boolean interceptFailHandle(int code) {
        return false;
    }

    @Override
    public void showToast(int code, String msg) {
        ToastUtil.toast(msg + "(" + code + ")");
    }
}
