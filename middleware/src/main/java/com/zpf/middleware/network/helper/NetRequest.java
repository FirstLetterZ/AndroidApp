package com.zpf.middleware.network.helper;

import com.zpf.middleware.network.call.UserCall;

/**
 * Created by ZPF on 2018/4/24.
 */

public class NetRequest {
    private static volatile NetRequest netRequest;
    private UserCall userCall;

    public NetRequest() {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        userCall = new UserCall(retrofitHelper);
    }

    private static NetRequest instance() {
        if (netRequest == null) {
            synchronized (NetRequest.class) {
                if (netRequest == null) {
                    netRequest = new NetRequest();
                }
            }
        }
        return netRequest;
    }

    public  static UserCall getUserCall() {
        return instance().userCall;
    }


}
