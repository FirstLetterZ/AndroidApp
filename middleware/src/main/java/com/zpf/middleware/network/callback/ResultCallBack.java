package com.zpf.middleware.network.callback;

import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.middleware.network.helper.HttpResult;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ZPF on 2018/4/23.
 */
public abstract class ResultCallBack<T> extends NetCallBack<HttpResult<T>> {
    public ResultCallBack() {
    }

    public ResultCallBack(int type) {
        super(type);
    }

    public ResultCallBack(BaseViewContainer container) {
        super(container);
    }

    public ResultCallBack(BaseViewContainer container, int type) {
        super(container, type);
    }

    @Override
    public void onNext(HttpResult<T> httpResult) {
        removeObservable();
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
                Observable<HttpResult<T>> newObservable = observable.retry();
                bindObservable(newObservable);
                newObservable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this);


            }
        } else {
            fail(httpResult.getCode(), httpResult.getMessage());
        }
    }

    @Override
    @Deprecated
    protected final void next(HttpResult<T> result) {
        response(result.getData());
    }

    protected abstract void response(T t);

}
