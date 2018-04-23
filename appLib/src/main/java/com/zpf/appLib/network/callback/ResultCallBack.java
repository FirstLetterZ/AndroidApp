package com.zpf.appLib.network.callback;

import com.zpf.appLib.network.helper.HttpResult;

/**
 * Created by ZPF on 2018/4/23.
 */
public abstract class ResultCallBack<T> extends NetCallBack<HttpResult<T>> {
    public ResultCallBack() {
        super();
    }

    public ResultCallBack(int type) {
        super(type);
    }

    @Override
    public void onNext(HttpResult<T> httpResult) {
        if (ignore()) {
            return;
        }
        if (httpResult == null) {
            fail(DATA_NULL, "返回数据为空");
        } else if (200 == httpResult.getCode()) {
            T t = httpResult.getData();
            if (checkNull(t)) {
                fail(DATA_NULL, "返回数据为空");
            } else {
                response(t);
                complete(true);
            }
        } else {
            String description = httpResult.getMessage();
            switch (httpResult.getCode()) {
                case 100:
                    description = "该用户不存在";
                    break;
                case 101:
                    description = "密码错误";
                    break;
                default:
            }
            fail(httpResult.getCode(), description);
        }
    }

    @Override
    @Deprecated
    protected final void next(HttpResult<T> result) {
        response(result.getData());
    }

    protected abstract void response(T t);

}
