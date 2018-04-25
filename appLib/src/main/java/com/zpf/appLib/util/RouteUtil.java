package com.zpf.appLib.util;

import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.constant.AppConst;

/**
 * Created by ZPF on 2018/4/21.
 */
public class RouteUtil implements RouteRule {
    private RouteRule routeRule;
    private static volatile RouteUtil routeUtil;

    public void init(RouteRule routeRule) {
        this.routeRule = routeRule;
    }

    public static RouteUtil instance() {
        if (routeUtil == null) {
            synchronized (RouteUtil.class) {
                if (routeUtil == null) {
                    routeUtil = new RouteUtil();
                }
            }
        }
        return routeUtil;
    }

    @Override
    public void pushActivity(String name, BaseViewContainer container) {
        if (checkState(name, container)) {
            routeRule.pushActivity(name, container);
        }
    }

    @Override
    public void pickActivity(String name, BaseViewContainer container) {
        if (checkState(name, container)) {
            routeRule.pickActivity(name, container);
        }
    }

    @Override
    public void pushActivityForResult(String name, BaseViewContainer container, int requestCode) {
        if (checkState(name, container)) {
            routeRule.pushActivityForResult(name, container, requestCode);
        }
    }

    private boolean checkState(String name, BaseViewContainer container) {
        if (routeUtil.routeRule == null) {
            routeUtil.routeRule = AppConst.instance().getRouteRule();
        }

        return routeRule != null && container != null && name != null;
    }
}
