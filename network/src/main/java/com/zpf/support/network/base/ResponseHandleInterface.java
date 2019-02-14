package com.zpf.support.network.base;

import com.zpf.support.network.model.HttpResult;

/**
 * Created by ZPF on 2019/2/14.
 */

public interface ResponseHandleInterface {

    //解析callback中的handleError
    HttpResult parsingException(Throwable e);

    boolean checkDataNull(Object data);

    //拦截callback中的fail方法
    boolean interceptFailHandle(int code);

    void showToast(int code, String msg);

}
