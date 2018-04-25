package com.zpf.middleware.network.callback;

import android.app.Dialog;
import android.net.ParseException;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.constant.StringConst;
import com.zpf.middleware.network.helper.NetRequest;
import com.zpf.middleware.util.PublicUtil;
import com.zpf.appLib.util.RouteUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by ZPF on 2018/4/23.
 */
public abstract class NetCallBack<T> implements Observer<T> {
    private int[] type = new int[]{0, 0, 0, 0};//{是否弹出错误提示，结果是否可为空，预留，预留}
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

    private Disposable disposable;
    private boolean isCancel = false;
    private Observable observable;
    private BaseViewContainer container;

    public NetCallBack(BaseViewContainer container) {
        this.container = container;
        container.addRequest(this);
    }

    public NetCallBack(BaseViewContainer container, @IntRange(from = 0, to = 16) int type) {
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

    public void bindObservable(Observable observable) {
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
        if (code == ERROR_REFRESH_OUT_DATA) {
            //去登录页面
            RouteUtil.instance().pickActivity(StringConst.VIEW_CLASS_LOGIN, container);
            return;
        } else if (code == ERROR_TOKEN_OUT_DATA) {
            refreshToken();
            return;
        } else if (code > -900 && showError(code, description) != null) {
            Dialog dialog = showError(code, description);
            if (dialog != null && !dialog.isShowing() && dialog.getWindow() != null) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if ((type[0] == 0) && !TextUtils.isEmpty(description)) {
            PublicUtil.toast(description + "(" + code + ")");
        }
        complete(false);
    }

    /**
     * 控制弹窗是否弹出
     * 需要复写
     */
    protected Dialog showError(int code, String description) {
        return null;
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
     * 取消订阅
     */
    public void cancel() {
        if (disposable != null && !disposable.isDisposed()) {
            isCancel = true;
            disposable.dispose();
            this.container = null;
            this.observable = null;
        }
    }

    /**
     * 刷新access_token
     */
    private void refreshToken() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", "123");
            jsonObject.put("userId", "123");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetRequest.getUserCall().refreshToken(jsonObject, new NetCallBack<JsonElement>(container) {
            @Override
            protected void next(JsonElement jsonElement) {
                if (!isCancel && observable != null) {
                    observable.retry();
                }
            }

            @Override
            protected void complete(boolean success) {
                if (!success) {
                    complete(false);
                }
            }
        });
    }

    //用来忽略所有操作
    protected boolean ignore() {
        return false;
    }

    protected abstract void next(T t);

    protected abstract void complete(boolean success);
}
