package com.zpf.appLib.network.call;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by ZPF on 2018/4/23.
 */

public abstract class BaseCall {

    private RequestBody getRequestBody(JSONObject params) {
        if (params == null) {
            params = new JSONObject();
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), params.toString());
    }

    public <T> Observer toSubscribe(Observable<T> o, Observer<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
        return s;
    }

}
