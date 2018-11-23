package com.zpf.support.util;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.zpf.api.GlobalConfigInterface;

/**
 * Created by ZPF on 2018/10/12.
 */
public class GlobalConfigUtil {
    private GlobalConfigInterface globalConfig;
    private boolean isDebug = true;

    public void init(Application application, GlobalConfigInterface globalConfig) {
        init("main", application, globalConfig);
    }

    public void init(String treadName, Application application, GlobalConfigInterface globalConfig) {
        if (TextUtils.equals(treadName, Thread.currentThread().getName())) {
            this.globalConfig = globalConfig;
            try {
                ApplicationInfo info = application.getApplicationInfo();
                isDebug = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            } catch (Exception e) {
                isDebug = false;
            }
        }
    }


    public boolean isDebug() {
        return isDebug;
    }
}
