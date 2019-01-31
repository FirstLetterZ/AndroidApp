package com.zpf.support.rxnetwork;

import com.zpf.tool.FileUtil;
import com.zpf.util.network.base.BaseCall;
import com.zpf.util.network.header.HeaderCarrier;
import com.zpf.util.network.model.ClientBuilder;

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

    public RxCall(HeaderCarrier headerCarrier) {
        super(headerCarrier);
    }

    @Override
    public ClientBuilder builder() {
        ClientBuilder builder = super.builder();
        builder.retrofitBuilder().addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder;
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
