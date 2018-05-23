package com.zpf.appLib.base;


import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by ZPF on 2018/4/23.
 */
public class BaseCall {

    protected RequestBody getRequestBody(JSONObject params) {
        if (params == null) {
            params = new JSONObject();
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), params.toString());
    }

    protected RequestBody getRequestBody(Map<String, Object> params) {
        JSONObject jsonObject;
        if (params == null) {
            jsonObject = new JSONObject();
        } else {
            jsonObject = new JSONObject(params);
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
    }

    protected <T> void toSubscribe(final Observable<T> o, final BaseCallBack<T> callBack) {
        callBack.bindObservable(o);
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack);
    }

}
