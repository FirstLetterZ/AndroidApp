package com.zpf.support.rxnetwork;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.support.network.base.BaseCallBack;
import com.zpf.support.network.base.ErrorCode;

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
        if (!isCancelled() && disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (isCancelled()) {
            d.dispose();
        }
    }

    @Override
    public void onNext(final T t) {
        if (isCancelled()) {
            return;
        }
        removeObservable();
        if (checkResponse(t)) {
            runInMain(new Runnable() {
                @Override
                public void run() {
                    try {
                        handleResponse(t);
                        complete(true, responseResult);
                    } catch (Exception e) {
                        handleError(e);
                    }
                }
            });
        } else {
            if (responseResult.getCode() == ErrorCode.RESPONSE_SUCCESS
                    || responseResult.getCode() == 0) {
                responseResult.setCode(ErrorCode.RESPONSE_ILLEGAL);
                responseResult.setMessage(getString(com.zpf.util.network.R.string.network_illegal_error));
            }
            fail(responseResult.getCode(), responseResult.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (isCancelled()) {
            return;
        }
        handleError(e);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public RxCallBack<T> toBind(IManager<ICancelable> manager) {
        super.toBind(manager);
        return this;
    }

    protected abstract void handleResponse(T response);
}
