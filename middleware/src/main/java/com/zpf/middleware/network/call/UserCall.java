package com.zpf.middleware.network.call;

import com.google.gson.JsonElement;
import com.zpf.appLib.base.BaseCall;
import com.zpf.middleware.network.api.UserApi;
import com.zpf.middleware.network.callback.NetCallBack;
import com.zpf.middleware.network.helper.RetrofitHelper;

import org.json.JSONObject;

/**
 * Created by ZPF on 2018/4/24.
 */

public class UserCall extends BaseCall {
    private UserApi mApi;

    public UserCall(RetrofitHelper helper) {
        mApi = helper.getRetrofit("123", UserApi.class);
    }

    public void refreshToken(JSONObject params, NetCallBack<JsonElement> callBack) {
        toSubscribe(mApi.refreshToken(getRequestBody(params)), callBack);
    }


}
