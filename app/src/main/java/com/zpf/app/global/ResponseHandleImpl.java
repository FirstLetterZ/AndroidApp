package com.zpf.app.global;

import com.zpf.api.IResultBean;
import com.zpf.tool.network.base.IResponseHandler;
import com.zpf.tool.network.model.ResponseResult;
import com.zpf.tool.toast.ToastUtil;

/**
 * Created by ZPF on 2019/3/25.
 */

public class ResponseHandleImpl implements IResponseHandler {

    private static class Instance {
        private static final ResponseHandleImpl mInstance = new ResponseHandleImpl();
    }

    private ResponseHandleImpl() {
    }

    public static ResponseHandleImpl get() {
        return Instance.mInstance;
    }

    @Override
    public ResponseResult<?> parsingException(Throwable e) {
        return null;
    }

    @Override
    public boolean checkDataNull(Object data) {
        return false;
    }

    @Override
    public boolean interceptFailHandle(int type, IResultBean<?> result) {
        return false;
    }

    @Override
    public void showHint(int code, String message) {
        ToastUtil.toast(message + "(" + code + ")");
    }
}
