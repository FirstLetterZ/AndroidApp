package com.zpf.support.network.base;

import android.accounts.AccountsException;
import android.net.ParseException;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.INeedManage;
import com.zpf.api.IResultBean;
import com.zpf.support.network.model.CustomException;
import com.zpf.support.network.model.RequestType;
import com.zpf.support.network.model.ResponseResult;
import com.zpf.support.network.util.Util;
import com.zpf.tool.global.CentralManager;
import com.zpf.util.network.R;

import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Created by ZPF on 2018/7/26.
 */
public abstract class BaseCallBack<T> implements ICancelable, INeedManage<ICancelable> {
    protected int type;//二进制开关
    private volatile boolean isCancel = false;
    protected IManager<ICancelable> manager;
    protected long bindId;
    private final IResponseHandler responseHandler = CentralManager.getInstance(IResponseHandler.class);
    protected ResponseResult<T> responseResult = new ResponseResult<>();

    public BaseCallBack() {
        this(0);
    }

    public BaseCallBack(int type) {
        this.type = type;
    }

    @Override
    public BaseCallBack<T> toBind(IManager<ICancelable> manager) {
        if (manager != null) {
            this.manager = manager;
            bindId = manager.bind(this);
        }
        return this;
    }

    @Override
    public boolean unBind(long bindId) {
        if (manager != null) {
            manager.remove(bindId);
            return true;
        }
        return false;
    }

    @Override
    public void cancel() {
        if (!isCancel) {
            isCancel = true;
            doCancel();
            removeObservable();
        }
    }

    protected void handleError(Throwable e) {
        String description;
        int code;
        if (e instanceof NullPointerException) {
            description = getString(R.string.network_data_null);
            code = ErrorCode.DATA_NULL;
        } else if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            description = exception.message();
            code = exception.code();
        } else if (e instanceof CustomException) {
            code = ((CustomException) e).getCode();
            description = e.getMessage();
        } else if (e instanceof UnknownHostException) {
            code = ErrorCode.HSOT_ERROR;
            description = getString(R.string.network_host_error);
        } else if (e instanceof AccountsException) {
            code = ErrorCode.ACCOUNT_ERROR;
            description = getString(R.string.network_account_error);
        } else if (e instanceof SSLHandshakeException) {
            code = ErrorCode.SSL_ERROR;
            description = getString(R.string.network_ssl_error);
        } else if (e instanceof JSONException
                || e instanceof ParseException) {
            code = ErrorCode.PARSE_ERROR;
            description = getString(R.string.network_parse_error);
        } else if (e instanceof ConnectException) {
            code = ErrorCode.CONNECT_ERROR;
            description = getString(R.string.network_connect_error);
        } else if (e instanceof TimeoutException
                || e instanceof SocketTimeoutException) {
            code = ErrorCode.TIMEOUT_ERROR;
            description = getString(R.string.network_timeout_error);
        } else if (e instanceof InterruptedIOException) {
            code = ErrorCode.INTERRUPTED_ERROR;
            description = getString(R.string.network_interrupted_error);
        } else if (e instanceof SocketException) {
            code = ErrorCode.SOCKET_ERROR;
            description = getString(R.string.network_socket_error);
        } else if (e instanceof IOException) {
            code = ErrorCode.IO_ERROR;
            description = getString(R.string.network_io_error);
        } else {
            IResultBean<?> parseResult = null;
            if (responseHandler != null) {
                parseResult = responseHandler.parsingException(e);
            }
            if (parseResult == null) {
                code = ErrorCode.NO_SERVER_CODE;
                description = e.getMessage();
                if (TextUtils.isEmpty(description)) {
                    description = e.toString();
                }
            } else {
                code = parseResult.getCode();
                description = parseResult.getMessage();
            }
        }
        fail(code, description);
    }

    /**
     * 要在主线程运行
     */
    protected void fail(final int code, final String message) {
        runInMain(new Runnable() {
            @Override
            public void run() {
                if (isCancel) {
                    return;
                }
                responseResult.setCode(code);
                responseResult.setMessage(message);
                if (responseHandler != null && responseHandler.interceptFailHandle(responseResult)) {
                    complete(false, responseResult);
                    return;
                }
                if (!RequestType.checkFlag(type, RequestType.FLAG_NO_TOAST) && !showCustomHint(code, message) && responseHandler != null) {
                    responseHandler.showHint(code, message);
                }
                complete(false, responseResult);
            }
        });
    }

    protected void removeObservable() {
        if (manager != null) {
            boolean realState = isCancel;
            manager.remove(bindId);
            manager = null;
            isCancel = realState;
        }
    }

    /**
     * 弹出自定义弹窗
     *
     * @return true--弹出了自定义弹窗；false--没有弹出弹窗
     */
    protected boolean showCustomHint(int code, String message) {
        return false;
    }

    /**
     * 运行在子线程
     * 返回数据内容预处理
     */
    protected boolean checkResponse(T result) {
        boolean check = true;
        responseResult.setCode(ErrorCode.RESPONSE_SUCCESS);
        if (result instanceof IResultBean) {
            check = ((IResultBean<?>) result).isSuccess();
            responseResult.setCode(((IResultBean<?>) result).getCode());
            responseResult.setMessage(((IResultBean<?>) result).getMessage());
        }
        if (check) {
            if (!RequestType.checkFlag(type, RequestType.FLAG_NULLABLE)) {//内容不能为空
                boolean isNull = result == null;
                if (!isNull) {
                    if (result instanceof IResultBean) {
                        isNull = ((IResultBean<?>) result).getData() == null;
                    } else {
                        isNull = responseHandler != null && responseHandler.checkDataNull(result);
                    }
                }
                if (isNull) {
                    responseResult.setCode(ErrorCode.DATA_NULL);
                    responseResult.setMessage(getString(R.string.network_data_null));
                    check = false;
                }
            }
        }
        responseResult.setData(result);
        return check;
    }

    /**
     * 当返回数据为空或者解析为空
     */
    protected final void onDataNull() {
        fail(ErrorCode.DATA_NULL, getString(R.string.network_data_null));
    }

    protected final void runInMain(Runnable runnable) {
        if (runnable == null || isCancel) {
            return;
        }
        CentralManager.runOnMainTread(runnable);
    }

    protected final String getString(int id) {
        return Util.getString(id);
    }

    @Override
    public boolean isCancelled() {
        return isCancel;
    }

    protected abstract void doCancel();

    protected abstract void complete(boolean success, @NonNull IResultBean<T> responseResult);

}
