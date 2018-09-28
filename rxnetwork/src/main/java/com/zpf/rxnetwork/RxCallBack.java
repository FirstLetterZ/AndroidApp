package com.zpf.rxnetwork;

import android.support.annotation.Nullable;

import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
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
        if (checkSuccessful(t)) {
            if (checkNull(t)) {
                onDataNull();
            } else if (checkResult(t)) {
                try {
                    handleResponse(t);
                    complete(true);
                } catch (Exception e) {
                    handleError(e);
                }
            } else {
                onResultIllegal(t);
            }
        } else {
            onUnsuccessful(t);
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

    /**
     * 检查是否为成功返回
     */
    protected boolean checkSuccessful(@Nullable T result) {
        return true;
    }

    /**
     * 不是成功返回
     */
    protected void onUnsuccessful(@Nullable T result) {

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
