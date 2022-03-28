package com.zpf.tool.network.base;

import com.zpf.api.IResultBean;

/**
 * Created by ZPF on 2019/2/14.
 */

public interface IResponseHandler {

    //解析callback中的handleError
    IResultBean<?> parsingException(Throwable e);

    boolean checkDataNull(Object data);

    //拦截callback中的fail方法
    boolean interceptFailHandle(IResultBean<?> result);

    void showHint(int code, String message);

}
