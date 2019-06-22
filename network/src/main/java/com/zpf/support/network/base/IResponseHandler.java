package com.zpf.support.network.base;


/**
 * Created by ZPF on 2019/2/14.
 */

public interface IResponseHandler {

    //解析callback中的handleError
    IResponseBean parsingException(Throwable e);

    boolean checkDataNull(Object data);

    //拦截callback中的fail方法
    boolean interceptFailHandle(IResponseBean result);

    void showHint(int code, String message);

}
