package com.zpf.appLib.network.callback;

import android.app.Dialog;
import android.net.ParseException;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zpf.appLib.util.PublicUtil;

import org.json.JSONException;

import java.net.ConnectException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by ZPF on 2018/4/23.
 */

public abstract class NetCallBack<T> implements Observer<T> {
    private int[] type = new int[]{0, 0, 0, 0};//{是否弹出错误提示，结果是否可为空，预留，预留}

    protected final int NO_SERVER_CODE = -900;
    protected final int DATA_NULL = -901;
    protected final int NETWORK_ERROR = -902;
    protected final int PARSE_ERROR = -903;
    protected final int SSL_ERROR = -904;


    private Disposable disposable;
    private boolean isCancel = false;

    public NetCallBack() {
    }

    public NetCallBack(@IntRange(from = 0, to = 16) int type) {
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

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (isCancel) {
            d.dispose();
        }
    }

    @Override
    public void onNext(T t) {
        if (!ignore()) {
            if (checkNull(t)) {
                fail(DATA_NULL, "返回数据为空");
            } else {
                next(t);
                complete(true);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
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
                code = PARSE_ERROR;
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
        if (code > -900 && showError(code, description) != null) {
            Dialog dialog = showError(code, description);
            if (dialog != null && !dialog.isShowing() && dialog.getWindow() != null) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if ((type[0] == 0) && !TextUtils.isEmpty(description)) {
            PublicUtil.show(description + "(" + code + ")");
        }
        complete(false);
    }

    //控制弹窗是否弹出
    protected Dialog showError(int code, String description) {
        return null;
    }

    //检查数据视为null或JsonNull
    boolean checkNull(Object value) {
        return (type[0] == 1) && (value == null ||
                ((value instanceof JsonElement) && ((JsonElement) value).isJsonNull()));
    }

    public void cancel() {
        if (disposable != null && !disposable.isDisposed()) {
            isCancel = true;
            disposable.dispose();
        }
    }

    //用来忽略所有操作
    protected boolean ignore() {
        return false;
    }

    protected abstract void next(T t);

    protected abstract void complete(boolean success);
}
