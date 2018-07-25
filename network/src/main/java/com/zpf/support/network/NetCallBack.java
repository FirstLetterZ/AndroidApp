package com.zpf.support.network;

import android.app.Dialog;
import android.support.annotation.NonNull;

import com.zpf.baselib.util.CallBackManager;

import java.util.regex.Pattern;

/**
 * @author ZPF
 *         类型为HttpResult<T>的返回处理
 */
public abstract class NetCallBack<T> extends BaseCallBack<HttpResult<T>> {

    public NetCallBack() {
    }

    public NetCallBack(int type) {
        super(type);
    }

    @Override
    public void onNext(HttpResult<T> httpResult) {
        if (httpResult == null) {
            fail(DATA_NULL, "返回数据为空", true);
        } else {
            if ("200".equals(httpResult.getCode())) {
                T t = httpResult.getData();
                if (checkNull(t)) {
                    fail(DATA_NULL, "返回数据为空", true);
                } else {
                    success(t);
                    complete(true);
                }
            } else {
                showFail(httpResult.getCode(), httpResult.getMessage());
            }
        }
    }

    private void showFail(String codeString, String description) {
        int code = NO_SERVER_CODE;
        if (codeString != null) {
            Pattern pattern = Pattern.compile("^[-]?[0-9]*");
            if (pattern.matcher(codeString).matches()) {
                try {
                    code = Integer.valueOf(codeString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        fail(code, description, true);
    }

    @Override
    public NetCallBack<T> bindToManager(@NonNull CallBackManager manager) {
        super.bindToManager(manager);
        return this;
    }

    @Override
    public NetCallBack<T> bindToManager(@NonNull CallBackManager manager, @NonNull Dialog dialog) {
        super.bindToManager(manager, dialog);
        return this;
    }

    @Override
    @Deprecated
    protected final void next(HttpResult<T> result) {
        success(result.getData());
    }

    protected abstract void success(T t);

}