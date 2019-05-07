package com.zpf.support.network.base;

import com.zpf.support.network.model.ResponseResult;

/**
 * Created by ZPF on 2019/2/14.
 */

public interface IResponseHandler {

    //解析callback中的handleError
    ResponseResult parsingException(Throwable e);

    boolean checkDataNull(Object data);

    //拦截callback中的fail方法
    boolean interceptFailHandle(ResponseResult result);

    void showHint(int code, String message);

}
