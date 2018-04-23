package com.zpf.appLib.util;

import com.zpf.appLib.base.BaseViewContainer;

/**
 * Created by ZPF on 2018/4/21.
 */

public interface RouteRule {
    void pushActivity(String name, BaseViewContainer container);

    void pickActivity(String name, BaseViewContainer container);

    void pushActivityForResult(String name, BaseViewContainer container, int requestCode);
}
