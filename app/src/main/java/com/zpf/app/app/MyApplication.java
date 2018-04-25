package com.zpf.app.app;

import android.app.Application;
import android.support.annotation.NonNull;

import com.zpf.app.config.RouteRuleList;
import com.zpf.appLib.constant.AppConst;
import com.zpf.appLib.util.AppInitInterface;
import com.zpf.appLib.util.FileUtil;
import com.zpf.appLib.util.RouteRule;
import com.zpf.middleware.constants.StringConst;

/**
 * Created by ZPF on 2018/4/25.
 */

public class MyApplication extends Application {
    private boolean isDebug;

    @Override
    public void onCreate() {
        super.onCreate();
        isDebug = "true".equals(FileUtil.getPropertiesValue("DEBUG", StringConst.CONFIG_FILE_DEFAULT));
        AppConst.init(new AppInitInterface() {
            @NonNull
            @Override
            public Application getApplication() {
                return MyApplication.this;
            }

            @Override
            public boolean isDebug() {
                return isDebug;
            }

            @NonNull
            @Override
            public String getDBFilePath() {
                return null;
            }

            @NonNull
            @Override
            public RouteRule getRouteRule() {
                return new RouteRuleList();
            }

            @Override
            public String getAppTag() {
                return "ZPF";
            }
        });
    }
}
