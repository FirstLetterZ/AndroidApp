package com.zpf.support.rxnetwork;
import com.zpf.support.network.model.RequestHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by ZPF on 2018/7/26.
 */
public class RxCallHelper extends RequestHelper {

    public static <T> Observer toSubscribe(Observable<T> o, Observer<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
        return s;
    }

    public static Observer forDownLoad(Observable<ResponseBody> o, Observer<Boolean> s, final String filePath) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) {
                        byte[] buf = new byte[2048];
                        int len;
                        FileOutputStream fos = null;
                        InputStream inputStream = responseBody.byteStream();
                        try {
                            fos = new FileOutputStream(filePath);
                            while ((len = inputStream.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                fos.flush();
                            }
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (inputStream != null) inputStream.close();
                                if (fos != null) fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
        return s;
    }

}
