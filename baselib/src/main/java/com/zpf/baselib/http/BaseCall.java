package com.zpf.baselib.http;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @author ZPF
 * @date 2018/07/02
 */
public class BaseCall {
    protected BaseNetworkBuilder mBuilder;

    public BaseCall(Map<String, Object> map) {
        mBuilder = new BaseNetworkBuilder();
        if (map != null && map.size() > 0) {
            mBuilder.setHeadParams(map);
        }
    }

    public RequestBody getRequestBody(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        JSONObject jsonObject = new JSONObject(params);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
    }

    public RequestBody getRequestObjectBody(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        JSONObject jsonObject = new JSONObject(params);
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonObject.toString());
    }

    public static MultipartBody.Part getMultipartBody(String partName, File file) {
        // 为file建立RequestBody实例
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public static Map<String, RequestBody> mapToRequestBody(Map<String, Object> maps) {
        Map<String, RequestBody> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue().toString());
            params.put(entry.getKey(), requestBody);
        }
        return params;
    }


    public <T> void toSubscribe(Observable<T> o, Observer<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public void toSubscribe(Observable<ResponseBody> o, BaseCallBack<byte[]> s) {
        o.subscribeOn(Schedulers.io())//subscribeOn和ObserOn必须在io线程，如果在主线程会出错
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())//需要
                .map(new Function<ResponseBody, byte[]>() {
                    @Override
                    public byte[] apply(ResponseBody responseBody) throws Exception {
                        try {
                            return responseBody.bytes();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public void forDownLoad(Observable<ResponseBody> o, final DownLoadCallBack s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation()) // 用于计算任务
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        s.saveFile(responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

}
