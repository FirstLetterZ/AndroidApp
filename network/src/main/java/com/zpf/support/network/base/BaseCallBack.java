package com.zpf.support.network.base;

import android.accounts.AccountsException;
import android.net.ParseException;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.INeedManage;
import com.zpf.support.network.model.CustomException;
import com.zpf.support.network.model.ResponseResult;
import com.zpf.tool.config.AppContext;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.MainHandler;
import com.zpf.util.network.R;

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
public abstract class BaseCallBack<T> implements ICancelable, INeedManage<ICancelable> {
    protected int[] type = new int[]{0, 0, 0, 0};//{不弹出错误提示，结果可为空，预留，预留}

    public static final int NOTTOAST = 1;
    public static final int NULLABLE = 2;
    public static final int NULLABLE_NOTTOAST = 3;

    private volatile boolean isCancel = false;
    protected IManager<ICancelable> manager;
    protected long bindId;
    private IResponseHandler responseHandler = GlobalConfigImpl.get().getGlobalInstance(IResponseHandler.class);
    protected ResponseResult<T> responseResult = new ResponseResult<>();

    public BaseCallBack() {
        this(0);
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
    public BaseCallBack toBind(IManager<ICancelable> manager) {
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
            IResponseBean parseResult = null;
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
                if (!showHint(code, message) && responseHandler != null && showHint()) {
                    responseHandler.showHint(code, message);
                }
                complete(false, responseResult);
            }
        });
    }

    protected void removeObservable() {
        if (manager != null) {
            manager.remove(bindId);
            manager = null;
        }
    }

    /**
     * 控制弹窗是否弹出
     */
    protected boolean showHint(int code, String message) {
        return false;
    }

    /**
     * 运行在子线程
     * 返回数据内容预处理
     */
    protected boolean checkResponse(T result) {
        boolean check = true;
        if (checkDataNull(result)) {
            responseResult.setCode(ErrorCode.DATA_NULL);
            responseResult.setMessage(getString(R.string.network_data_null));
            check = isNullable();
        }
        if (check && (result instanceof IResponseBean)) {
            check = ((IResponseBean) result).isSuccess();
            responseResult.setCode(((IResponseBean) result).getCode());
            responseResult.setMessage(((IResponseBean) result).getMessage());
        }
        responseResult.setData(result);
        return check;
    }

    protected boolean checkDataNull(T result) {
        if (result == null) {
            return true;
        } else if (result instanceof IResponseBean) {
            return ((IResponseBean) result).getData() == null;
        } else {
            return responseHandler != null && responseHandler.checkDataNull(result);
        }
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
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            MainHandler.get().post(runnable);
        }
    }

    protected final String getString(int id) {
        try {
            return AppContext.get().getString(id);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 检查是否弹窗
     */
    protected final boolean showHint() {
        return type[0] == 0;
    }

    /**
     * 检查是否数据内容可为空
     */
    protected final boolean isNullable() {
        return type[1] == 1;
    }


    @Override
    public boolean isCancelled() {
        return isCancel;
    }

    protected abstract void doCancel();

    protected abstract void complete(boolean success, @NonNull IResponseBean<T> responseResult);

}
