package com.zpf.support.rxnetwork;

import com.zpf.api.ICallback;
import com.zpf.api.IManager;
import com.zpf.support.network.base.BaseCallBack;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class RxCallBack<T> extends BaseCallBack<T> implements Observer<T> {
    protected Disposable disposable;

    public RxCallBack() {
        super();
    }

    public RxCallBack(int type) {
        super(type);
    }

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
        if (isCancel()) {
            return;
        }
        removeObservable();
        if (checkNull(t)) {
            onDataNull();
        } else if (checkResultSuccess(t)) {
            try {
                handleResponse(t);
                complete(true);
            } catch (Exception e) {
                handleError(e);
            }
        } else {
            onResultIllegal(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (isCancel()) {
            return;
        }
        handleError(e);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public RxCallBack<T> toBind(IManager<ICallback> manager) {
        super.toBind(manager);
        return this;
    }

    protected abstract void handleResponse(T response);
}
