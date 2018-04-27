package com.zpf.middleware.network.callback;

import android.app.Dialog;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.zpf.appLib.base.BaseCallBack;
import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.util.RouteUtil;
import com.zpf.middleware.constants.StringConst;
import com.zpf.middleware.network.helper.NetRequest;
import com.zpf.middleware.util.PublicUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ZPF on 2018/4/23.
 */
public abstract class NetCallBack<T> extends BaseCallBack<T> {
    public NetCallBack() {
    }

    public NetCallBack(int type) {
        super(type);
    }

    public NetCallBack(BaseViewContainer container) {
        super(container);
    }

    public NetCallBack(BaseViewContainer container, int type) {
        super(container, type);
    }

    @Override
    public void onNext(T t) {
        removeObservable();
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
