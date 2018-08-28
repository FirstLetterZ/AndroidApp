package com.zpf.rxnetwork;

import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.network.base.BaseCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ZPF on 2018/7/26.
 */

public abstract class RxCallBack<T> extends BaseCallBack implements Observer<T> {
    protected Disposable disposable;

    @Override
    protected void doCancel() {
        if (!isCancel() && disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (isCancel()) {
            d.dispose();
        }
    }

    @Override
    public void onNext(T t) {
        removeObservable();
        if (checkNull(t)) {
            fail(DATA_NULL, "返回数据为空", true);
        } else {
            try {
                handleResponse(t);
                complete(true);
            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public RxCallBack<T> bindToManager(CallBackManagerInterface manager) {
        super.bindToManager(manager);
        return this;
    }

    @Override
    public RxCallBack<T> bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog) {
        super.bindToManager(manager, dialog);
        return this;
    }

    protected abstract void handleResponse(T response);
}
