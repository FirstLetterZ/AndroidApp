package com.zpf.support.network.base;

import android.accounts.AccountsException;
import android.net.ParseException;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.zpf.api.ICallback;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.OnDestroyListener;
import com.zpf.support.network.model.CustomException;
import com.zpf.support.network.model.HttpResult;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.MainHandler;

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
public abstract class BaseCallBack<T> implements ICallback, OnDestroyListener {
    protected int[] type = new int[]{0, 0, 0, 0};//{不弹出错误提示，结果可为空，预留，预留}

    public static final int NOTTOAST = 1;
    public static final int NULLABLE = 2;
    public static final int NULLABLE_NOTTOAST = 3;

    private volatile boolean isCancel = false;
    protected IManager<ICallback> manager;
    protected ICustomWindow safeWindow;
    protected long bindId;
    private ResponseHandleInterface responseHandler;

    public BaseCallBack() {
        this(0);
    }

    public BaseCallBack(int type) {
        setType(type);
        responseHandler = GlobalConfigImpl.get().getGlobalInstance(ResponseHandleInterface.class);
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
    public BaseCallBack toBind(IManager<ICallback> manager) {
        if (manager != null) {
            this.manager = manager;
            bindId = manager.bind(this);
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
            HttpResult parseResult = null;
            if (responseHandler != null) {
                parseResult = responseHandler.parsingException(e);
            }
            if (parseResult == null) {
                code = ErrorCode.NO_SERVER_CODE;
                description = e.toString();
            } else {
                code = parseResult.getCode();
                description = parseResult.getMessage();
            }
        }
        fail(code, description);
    }

    /**
     * @param code        错误码
     * @param description 错误描述
     */
    protected void fail(final int code, final String description) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            MainHandler.get().post(new Runnable() {
                @Override
                public void run() {
                    if (!isCancel) {
                        fail(code, description);
                    }
                }
            });
            return;
        }
        if (responseHandler != null && responseHandler.interceptFailHandle(code)) {
            complete(false);
            return;
        }
        if (!showDialog(code, description) && responseHandler != null && autoToast()) {
            if (TextUtils.isEmpty(description)) {
                responseHandler.showToast(code, "请求失败，请稍后重试");
            } else {
                responseHandler.showToast(code, description);
            }
        }
        complete(false);
    }

    protected void removeObservable() {
        if (manager != null) {
            manager.cancel(bindId);
            manager = null;
        }
        if (safeWindow != null && safeWindow.isShowing()) {
            try {
                safeWindow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            safeWindow = null;
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
        } else if (value instanceof HttpResult) {
            return ((HttpResult) value).getData() == null;
        } else {
            return responseHandler != null && responseHandler.checkDataNull(value);
        }
    }

    /**
     * 控制弹窗是否弹出
     */
    protected boolean showDialog(int code, String description) {
        return false;
    }

    /**
     * 运行在子线程
     * 返回数据内容预处理
     */
    protected void preProcessResult(T result) {
    }

    /**
     * 运行在子线程
     * 返回数据内容是否满足成功条件
     */
    protected boolean checkResultSuccess(T result) {
        return true;
    }

    /**
     * 运行在主线程
     * 返回数据内容不满足成功条件的处理
     */
    protected void onResultIllegal(T result) {
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
