package com.zpf.support.network.base;

import android.accounts.AccountsException;
import android.app.Dialog;
import android.net.ParseException;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zpf.tool.ToastUtil;
import com.zpf.api.CallBackInterface;
import com.zpf.api.CallBackManagerInterface;
import com.zpf.api.SafeWindowInterface;
import com.zpf.support.network.model.CustomException;
import com.zpf.support.network.model.HttpResult;
import com.zpf.tool.config.GlobalConfigImpl;

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
        String description;
        int code;
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            description = exception.response().message();
            code = exception.response().code();
        } else if (e instanceof CustomException) {
            code = ((CustomException) e).getCode();
            description = e.getMessage();
        } else if (e instanceof AccountsException) {
            code = ErrorCode.ACCOUNT_ERROR;
            description = "账号验证失败";
        } else if (e instanceof SSLHandshakeException) {
            code = ErrorCode.SSL_ERROR;
            description = "网络证书验证失败";
        } else if (e instanceof JSONException
                || e instanceof ParseException) {
            code = ErrorCode.PARSE_ERROR;
            description = "数据解析异常";
        } else if (e instanceof ConnectException) {
            code = ErrorCode.CONNECT_ERROR;
            description = "连接服务器失败";
        } else if (e instanceof TimeoutException
                || e instanceof SocketTimeoutException) {
            code = ErrorCode.TIMEOUT_ERROR;
            description = "连接超时，请稍后再试";
        } else if (e instanceof InterruptedIOException) {
            code = ErrorCode.INTERRUPTED_ERROR;
            description = "连接中断，请稍后再试";
        } else if (e instanceof SocketException) {
            code = ErrorCode.NETWORK_ERROR;
            description = "网络连接异常";
        } else if (e instanceof IOException) {
            code = ErrorCode.IO_ERROR;
            description = "数据流异常，解析失败";
        } else {
            Object result = GlobalConfigImpl.get().invokeMethod(this, "checkNetError", e);
            if (result != null && result instanceof HttpResult) {
                code = ((HttpResult) result).getCode();
                description = ((HttpResult) result).getMessage();
            } else {
                code = ErrorCode.NO_SERVER_CODE;
                description = e.toString();
            }
        }
        fail(code, description);
    }

    /**
     * @param code        错误码
     * @param description 错误描述
     */
    protected void fail(int code, String description) {
        Object result = GlobalConfigImpl.get().invokeMethod(this, "checkNetFail", code);
        if (result != null && result instanceof Boolean) {
            if ((boolean) result) {
                complete(false);
                return;
            }
        }
        boolean showDialog = false;
        if (code > -900) {
            Dialog dialog = showDialog(code, description);
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
        complete(false);
    }

    //控制弹窗是否弹出
    protected Dialog showDialog(int code, String description) {
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
     * 检查数据视为null或JsonNull
     */
    protected final boolean checkNull(Object value) {
        if (type[1] == 1) {
            return false;
        } else if (value == null) {
            return true;
        }  else if (value instanceof HttpResult) {
            return ((HttpResult) value).getData() == null;
        } else {
            Object result = GlobalConfigImpl.get().invokeMethod(this, "checkNetNull", value);
            return result != null && result instanceof Boolean && (boolean) result;
        }
    }

    /**
     * 检查内容是否满足自定义的条件
     */
    protected boolean checkResult(@Nullable T result) {
        return true;
    }

    /**
     * 如果不满足自定义的检查条件则执行下面的方法
     */
    protected void onResultIllegal(@Nullable T result) {

    }

    /**
     * 当返回数据为空或者解析为空
     */
    protected void onDataNull() {
        fail(ErrorCode.DATA_NULL, "返回数据为空");
    }

    /**
     * 检查是否弹窗
     */
    private boolean autoToast() {
        return type[0] == 0;
    }

    public boolean isCancel() {
        return isCancel;
    }

    protected abstract void doCancel();

    protected abstract void complete(boolean success);

}
