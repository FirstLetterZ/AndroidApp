package com.zpf.rxnetwork;

import com.zpf.support.generalUtil.FileUtil;
import com.zpf.support.network.base.BaseCall;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by ZPF on 2018/7/26.
 */
public class RxCall extends BaseCall {

    public RxCall(Map<String, Object> map) {
        super(map);
        mBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public <T> void toSubscribe(Observable<T> o, Observer<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public void forDownLoad(Observable<ResponseBody> o, Observer<Boolean> s, final String filePath) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        return FileUtil.writeToFile(filePath, responseBody.byteStream());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

}
