package com.zpf.support.network.base;

import android.accounts.AccountsException;
import android.app.Application;
import android.app.Dialog;
import android.net.ParseException;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.zpf.support.generalUtil.AppContext;
import com.zpf.support.generalUtil.ToastUtil;
import com.zpf.support.interfaces.CallBackInterface;
import com.zpf.support.interfaces.CallBackManagerInterface;
import com.zpf.support.interfaces.GlobalConfigInterface;
import com.zpf.support.interfaces.SafeWindowInterface;
import com.zpf.support.network.model.CustomException;

import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class BaseCallBack<T> implements CallBackInterface {
    protected int[] type = new int[]{0, 0, 0, 0};//{不弹出错误提示，结果可为空，预留，预留}

    public static final int NOTTOAST = 1;
    public static final int NULLABLE = 2;
    public static final int NULLABLE_NOTTOAST = 3;

    protected static final int DATA_NULL = -900;
    public final int NO_SERVER_CODE = -901;
    protected final int SSL_ERROR = -902;
    protected final int PARSE_ERROR = -903;
    protected final int ACCOUNT_ERROR = -904;
    protected final int NETWORK_ERROR = -910;
    protected final int CONNECT_ERROR = -911;
    protected final int TIMEOUT_ERROR = -912;
    protected final int INTERRUPTED_ERROR = -913;
    protected final int IO_ERROR = -920;

    private volatile boolean isCancel = false;
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
        } else if (e instanceof CustomException) {
            code = ((CustomException)e).getCode();
            description = e.getMessage();
        } else if (e instanceof AccountsException) {
            code = ACCOUNT_ERROR;
            description = "账号验证失败";
        } else if (e instanceof SSLHandshakeException) {
            code = SSL_ERROR;
            description = "网络证书验证失败";
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof MalformedJsonException) {
            code = PARSE_ERROR;
            description = "数据解析异常";
        } else if (e instanceof ConnectException) {
            code = CONNECT_ERROR;
            description = "连接服务器失败";
        } else if (e instanceof TimeoutException
                || e instanceof SocketTimeoutException) {
            code = TIMEOUT_ERROR;
            description = "连接超时，请稍后再试";
        } else if (e instanceof InterruptedIOException) {
            code = INTERRUPTED_ERROR;
            description = "连接中断，请稍后再试";
        } else if (e instanceof SocketException) {
            code = NETWORK_ERROR;
            description = "网络连接异常";
        } else if (e instanceof IOException) {
            code = IO_ERROR;
            description = "数据流异常，解析失败";
        } else {
            code = NO_SERVER_CODE;
        }
        if (code != NO_SERVER_CODE) {
            description = description + "\n" + e.getMessage();
        }
        fail(code, description, true);
    }

    /**
     * @param code        错误码
     * @param description 错误描述
     * @param complete    是否执行complete（）
     */
    protected void fail(int code, String description, boolean complete) {
        Application application = AppContext.get();
        if (application != null && application instanceof GlobalConfigInterface) {
            //检查登录信息是否失效
            Object result = ((GlobalConfigInterface) application).invokeMethod(
                    this, "checkLoginEffective", code);
            if (result != null && result instanceof Boolean) {
                if ((boolean) result && complete) {
                    complete(false);
                }
                return;
            }
        }
        boolean showDialog = code > -900;
        if (showDialog) {
            Dialog dialog = showError(code, description);
            showDialog = dialog != null;
            if (showDialog && !dialog.isShowing() && dialog.getWindow() != null) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!showDialog && autoToast()) {
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
     * 检查内容是否满足自定义的条件
     *
     * @param result
     * @return
     */
    protected boolean checkResult(@Nullable T result) {
        return true;
    }

    /**
     * 如果不满足自定义的检查条件则执行下面的方法
     *
     * @param result
     */
    protected void onResultIllegal(@Nullable T result) {

    }

    /**
     * 当返回数据为空或者解析为空
     */
    protected void onDataNull() {
        fail(DATA_NULL, "返回数据为空", true);
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
