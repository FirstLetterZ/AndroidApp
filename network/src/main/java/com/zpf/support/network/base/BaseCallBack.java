package com.zpf.support.network.base;

import android.accounts.AccountsException;
import android.app.Dialog;
import android.net.ParseException;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.zpf.support.generalUtil.ToastUtil;
import com.zpf.support.interfaces.CallBackInterface;
import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.SafeWindowInterface;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Created by ZPF on 2018/7/26.
 */

public abstract class BaseCallBack implements CallBackInterface {
    protected int[] type = new int[]{0, 0, 0, 0};//{不弹出错误提示，结果可为空，预留，预留}

    public static final int NOTTOAST = 1;
    public static final int NULLABLE = 2;
    public static final int NULLABLE_NOTTOAST = 3;

    protected final int NO_SERVER_CODE = -900;
    protected final int DATA_NULL = -901;
    protected final int NETWORK_ERROR = -902;
    protected final int PARSE_ERROR = -903;
    protected final int SSL_ERROR = -904;
    protected final int IO_ERROR = -905;

    private boolean isCancel = false;
    protected CallBackManagerInterface manager;
    protected SafeWindowInterface dialog;
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
    public CallBackInterface bindToManager(CallBackManagerInterface manager) {
        if (manager != null) {
            this.manager = manager;
            bindId = manager.addCallBack(this);
        }
        return this;
    }

    @Override
    public CallBackInterface bindToManager(CallBackManagerInterface manager, SafeWindowInterface dialog) {
        if (manager != null) {
            this.manager = manager;
            bindId = manager.addCallBack(this);
        }
        if (dialog != null && dialog.isShowing()) {
            this.dialog = dialog;
            dialog.bindRequest(this);
        }
        return this;
    }

    @Override
    public void cancel() {
        if (!isCancel) {
            isCancel = true;
            doCancel();
            removeObservable();
        }
    }

    @Override
    public void onDestroy() {
        cancel();
    }

    protected void handleError(Throwable e) {
        removeObservable();
        e.printStackTrace();
        String description = e.toString();
        int code;
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            description = exception.response().message();
            code = exception.response().code();
        } else if (e instanceof AccountsException) {
            code = SSL_ERROR;
            description = "证书验证失败";
        } else if (e instanceof SSLHandshakeException) {
            code = SSL_ERROR;
            description = "证书验证失败";
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof MalformedJsonException) {
            code = PARSE_ERROR;
            description = "数据解析异常";
        } else if (e instanceof SocketException) {
            code = NETWORK_ERROR;
            description = "网络连接异常";
        } else if (e instanceof TimeoutException) {
            code = NETWORK_ERROR;
            description = "连接超时，请稍后再试";
        } else if (e instanceof IOException) {
            code = IO_ERROR;
            description = "数据流异常，解析失败";
        } else if (description.contains("com.google.gson.JsonNull")) {
            description = "返回数据为空";
            code = DATA_NULL;
        } else if (description.contains("Unable to resolve host")) {
            description = "网络连接异常";
            code = NETWORK_ERROR;
        } else {
            code = NO_SERVER_CODE;
            String msg = e.getMessage();
            if (!TextUtils.isEmpty(msg)) {
                description = msg;
            }
        }
        fail(code, description, true);
    }

    /**
     * @param code        错误码
     * @param description 错误描述
     * @param complete    是否执行complete（）
     */
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
            ToastUtil.toast(description + "(" + code + ")");
        }
        if (complete) {
            complete(false);
        }
    }

    //控制弹窗是否弹出
    protected Dialog showError(int code, String description) {
        return null;
    }

    protected void removeObservable() {
        if (manager != null) {
            manager.removeCallBack(bindId);
            manager = null;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.bindRequest(null);
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog = null;
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

    public boolean isCancel() {
        return isCancel;
    }

    protected abstract void doCancel();

    protected abstract void complete(boolean success);

}
