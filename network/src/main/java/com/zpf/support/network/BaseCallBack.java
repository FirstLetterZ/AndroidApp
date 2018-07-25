package com.zpf.support.network;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ParseException;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zpf.baselib.interfaces.CallBackInterface;
import com.zpf.baselib.interfaces.CallBackManagerInterface;
import com.zpf.baselib.util.PublicUtil;

import org.json.JSONException;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * @author ZPF
 * 网络返回处理
 */
public abstract class BaseCallBack<T> implements Observer<T>, CallBackInterface {
    protected int[] type = new int[]{0, 0, 0, 0};//{不弹出错误提示，结果可为空，预留，预留}

    public static final int NOTTOAST = 1;
    public static final int NULLABLE = 2;
    public static final int NULLABLE_NOTTOAST = 3;

    protected final int NO_SERVER_CODE = -900;
    protected final int DATA_NULL = -901;
    protected final int NETWORK_ERROR = -902;
    protected final int PARSE_ERROR = -903;
    protected final int SSL_ERROR = -904;

    private boolean isCancel = false;
    protected Disposable disposable;
    protected CallBackManagerInterface manager;
    protected Dialog dialog;
    protected long bindId;

    public BaseCallBack() {
        setType(0);
    }

    public BaseCallBack(int type) {
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

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (isCancel) {
            d.dispose();
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        removeObservable();
        e.printStackTrace();
        String msg = e.getMessage();
        String description = e.toString();
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
        } else {
            if (e instanceof ConnectException) {
                code = NETWORK_ERROR;
                description = "网络连接异常";
            } else if (e instanceof TimeoutException) {
                code = NETWORK_ERROR;
                description = "连接超时，请稍后再试";
            } else if (description.contains("com.google.gson.JsonNull")) {
                description = "返回数据为空";
                code = DATA_NULL;
            } else if (description.contains("Unable to resolve host")) {
                description = "网络连接异常";
                code = NETWORK_ERROR;
            } else {
                code = NO_SERVER_CODE;
                if (!TextUtils.isEmpty(msg)) {
                    description = msg;
                }
            }
        }
        fail(code, description, true);
    }

    @Override
    public void onNext(T result) {
        removeObservable();
        if (checkNull(result)) {
            fail(DATA_NULL, "返回数据为空", true);
        } else {
            next(result);
            complete(true);
        }
    }

    //外部调用
    protected void fail(int code, String description) {
        fail(code, description, false);
    }

    //内部调用
    protected void fail(int code, String description, boolean complete) {
        if (code > -900 && showError(code, description) != null) {
            Dialog dialog = showError(code, description);
            if (dialog != null && !dialog.isShowing() && dialog.getWindow() != null) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (autoToast()) {
            if (TextUtils.isEmpty(description)) {
                description = "请求失败，请稍后重试";
            }
            PublicUtil.toast(description + "(" + code + ")");
        }
        if (complete) {
            complete(false);
        }
    }

    /**
     * 检查是否弹窗
     */
    private boolean autoToast() {
        return type[0] == 0;
    }

    /**
     * 检查数据视为null或JsonNull
     */
    protected final boolean checkNull(Object value) {
        return type[1] != 1 && (value == null || ((value instanceof JsonElement) && ((JsonElement) value).isJsonNull()));
    }

    //控制弹窗是否弹出
    protected Dialog showError(int code, String description) {
        return null;
    }

    /**
     * 取消生命周期绑定
     */
    protected void removeObservable() {
        if (manager != null) {
            manager.removeCallBack(bindId);
            manager = null;
        }
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.setOnDismissListener(null);
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        }
    }

    @Override
    public BaseCallBack<T> bindToManager(CallBackManagerInterface manager) {
        if (manager != null) {
            this.manager = manager;
            bindId = manager.addCallBack(this);
        }
        return this;
    }

    @Override
    public BaseCallBack<T> bindToManager(CallBackManagerInterface manager, Dialog dialog) {
        if (manager != null) {
            this.manager = manager;
            bindId = manager.addCallBack(this);
        }
        if (dialog != null && dialog.isShowing()) {
            this.dialog = dialog;
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancel();
                }
            });
        }
        return this;
    }

    @Override
    public void onDestroy() {
        cancel();
    }

    protected abstract void next(T t);

    protected abstract void complete(boolean success);
}
