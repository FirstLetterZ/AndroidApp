package com.zpf.support.util;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpf.support.api.RouteIntereface;

/**
 * 路由工具
 * Created by ZPF on 2018/8/26.
 */
public class RouteUtil implements RouteIntereface {
    private volatile RouteUtil realRoute;
    private static volatile RouteUtil routeUtil;

    public static RouteUtil get() {
        if (routeUtil == null) {
            synchronized (RouteUtil.class) {
                if (routeUtil == null) {
                    routeUtil = new RouteUtil();
                }
            }
        }
        return routeUtil;
    }

    public void init(RouteUtil routeUtil) {
        this.realRoute = routeUtil;
    }

    @Override
    public void startActivity(Context context, int targetNumber, @Nullable Intent intent) {
        if (realRoute != null) {
            realRoute.startActivity(context, targetNumber, intent);
        }
    }

    @Override
    public void startActivity(Context context, int targetNumber, @Nullable Intent intent, @Nullable Bundle options) {
        if (realRoute != null) {
            realRoute.startActivity(context, targetNumber, intent, options);
        }
    }

    @Override
    public void startActivityForResult(ComponentCallbacks componentCallbacks, int targetNumber, @Nullable Intent intent, int requestCode) {
        if (realRoute != null) {
            realRoute.startActivityForResult(componentCallbacks, targetNumber, intent, requestCode);
        }
    }

    @Override
    public void startActivityForResult(ComponentCallbacks componentCallbacks, int targetNumber, @Nullable Intent intent, int requestCode, @Nullable Bundle options) {
        if (realRoute != null) {
            realRoute.startActivityForResult(componentCallbacks, targetNumber, intent, requestCode, options);
        }
    }
}
