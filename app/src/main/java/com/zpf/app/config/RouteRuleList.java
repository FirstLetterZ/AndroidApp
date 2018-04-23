package com.zpf.app.config;

import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.base.BaseViewLayout;
import com.zpf.appLib.util.RouteRule;
import com.zpf.appLib.constant.AppConst;
import com.zpf.login.layout.IntroduceView;
import com.zpf.login.layout.LoginView;

/**
 * Created by ZPF on 2018/4/21.
 */

public class RouteRuleList implements RouteRule {
    @Override
    public void pushActivity(String name, BaseViewContainer container) {
        container.pushActivity(getViewByName(name));
    }

    @Override
    public void pickActivity(String name, BaseViewContainer container) {
        container.pickActivity(getViewByName(name));
    }

    @Override
    public void pushActivityForResult(String name, BaseViewContainer container, int requestCode) {
        container.pushActivityForResult(getViewByName(name), requestCode);
    }

    private Class<? extends BaseViewLayout> getViewByName(String name) {
        Class<? extends BaseViewLayout> result = null;
        if (name != null) {
            switch (name) {
                case AppConst.VIEW_CLASS_LOGIN:
                    result = LoginView.class;
                    break;
                case AppConst.VIEW_CLASS_INTRODUCE:
                    result = IntroduceView.class;
                    break;
            }
        }
        return result;
    }
}
