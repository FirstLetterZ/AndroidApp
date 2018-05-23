package com.zpf.appLib.base;

import android.net.ParseException;
import android.support.annotation.IntRange;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by ZPF on 2018/4/25.
 */

public class BaseCallBack<T> implements Observer<T> {
    protected int[] type = new int[]{0, 0, 0, 0};//{是否弹出错误提示，结果是否可为空，预留，预留}
    //异常返回code
    protected final int NO_SERVER_CODE = -900;
    protected final int DATA_NULL = -901;
    protected final int NETWORK_ERROR = -902;
    protected final int PARSE_ERROR = -903;
    protected final int SSL_ERROR = -904;

    //特殊异常返回code
    public final int ERROR_TOKEN_OUT_DATA = 1401;//access_token过期
    public final int ERROR_REFRESH_OUT_DATA = 1402;//refresh_token过期

    //type值
    public static final int TYPE_NOTTOAST = 1;
    public static final int TYPE_NULLABLE = 2;
    public static final int TYPE_ALL = 3;

    protected Disposable disposable;
    protected boolean isCancel = false;
    protected Observable<T> observable;
    protected BaseViewContainer container;
    protected int bindId;

    public BaseCallBack() {

    }

    public BaseCallBack(@IntRange(from = 0, to = 16) int type) {
        setType(type);
    }

    public BaseCallBack(BaseViewContainer container) {
        this.container = container;
        bindId = container.addRequest(this);
    }

    public BaseCallBack(BaseViewContainer container, @IntRange(from = 0, to = 16) int type) {
        this.container = container;
        setType(type);
    }

    public void setType(@IntRange(from = 0, to = 16) int type) {
        int a = type * 2;
        int b;
        int n = 0;
        while (true) {
            a = a / 2;
            b = a % 2;
            if (b > 0) {
                this.type[n] = 1;
            } else {
                this.type[n] = 0;
            }
            n++;
            if (a == 0 || n == this.type.length) {
                break;
            }
        }
    }

    public void pretreatment(T t) {

    }

    public void bindObservable(Observable<T> observable) {
        this.observable = observable;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (isCancel) {
            d.dispose();
        }
    }

    @Override
    public void onNext(T t) {
        removeObservable();
    }

    @Override
    public void onError(Throwable e) {
        removeObservable();
        e.printStackTrace();
        if (!ignore()) {
            String description;
            int code;
            if (e instanceof HttpException) {
                HttpException exception = (HttpException) e;
                description = exception.response().message();
                code = exception.response().code();
            } else if (e instanceof SSLHandshakeException) {
                code = SSL_ERROR;
                description = "证书验证失败";
            } else if (e instanceof JsonParseException
                    || e instanceof JSONException
                    || e instanceof ParseException) {
                code = PARSE_ERROR;
                description = "数据解析异常";
            } else if (e instanceof ConnectException) {
                code = NETWORK_ERROR;
                description = "网络连接异常";
            } else {
                code = NO_SERVER_CODE;
                description = "网络连接异常";
            }
            fail(code, description);
        }
    }

    @Override
    public void onComplete() {
    }

    protected void fail(int code, String description) {
    }

    /**
     * 检查数据视为null或JsonNull
     */
    protected final boolean checkNull(Object value) {
        return (type[0] == 1) && (value == null ||
                ((value instanceof JsonElement) && ((JsonElement) value).isJsonNull()));
    }

    public final boolean isCancel() {
        return isCancel;
    }

    /**
     * 取消生命周期绑定
     */
    protected void removeObservable() {
        if (container != null) {
            container.removeRequest(bindId);
            container = null;
        }
    }

    /**
     * 取消订阅
     */
    public void cancel() {
        if (disposable != null && !disposable.isDisposed()) {
            isCancel = true;
            removeObservable();
            disposable.dispose();
            observable = null;
        }
    }

    //用来忽略所有操作
    protected boolean ignore() {
        return false;
    }

}
