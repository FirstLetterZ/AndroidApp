package com.zpf.appLib.constant;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.zpf.appLib.util.AppInitInterface;
import com.zpf.appLib.util.FileUtil;
import com.zpf.appLib.util.JsonUtil;
import com.zpf.appLib.util.PathInfo;
import com.zpf.appLib.util.RouteRule;
import com.zpf.modelsqlite.CacheInitInterface;
import com.zpf.modelsqlite.CacheUtil;

/**
 * 全局常量（定义后不再修改）
 * Created by ZPF on 2018/4/13.
 */
public class AppConst implements AppInitInterface {

    //常量部分
    public static final int REQUEST_CODE_DEFAULT = 65535;
    public static final int REQUEST_CODE_EXIT_APP = 65534;
    public static final int REQUEST_CODE_CHECK_TARGET = 65533;
    public static final int REQUEST_CODE_CHECK_PERMISSION = 65532;

    public static final String CACHE_SP_KEY = "cache_map_";
    public static final String CACHE_SP_FILE_NAME = "cache_sp_data";

    public static final int DEF_INT = 0;
    public static final long DEF_LONG = 0L;
    public static final float DEF_FLOAT = 0.0f;
    public static final boolean DEF_BOOLEAN = false;
    public static final String DEF_STRING = "";

    //代理部分,动态常量
    private static volatile AppConst appConst;
    private AppInitInterface appInitInterface;
    private PathInfo pathInfo;

    public static void init(@NonNull AppInitInterface initInterface) {
        instance().appInitInterface = initInterface;
        CacheUtil.init(new CacheInitInterface() {
            @Override
            public Context getContext() {
                return instance().appInitInterface.getApplication();
            }

            @Override
            public String getDataBaseFolderPath() {
                return instance().appInitInterface.getDBFilePath();
            }

            @Override
            public String toJson(Object o) {
                return JsonUtil.toString(o);
            }

            @Override
            public Object fromJson(String s, Class<?> aClass) {
                return JsonUtil.fromJson(s, aClass);
            }
        });
    }

    public static AppConst instance() {
        if (appConst == null) {
            synchronized (AppConst.class) {
                if (appConst == null) {
                    appConst = new AppConst();
                }
            }
        }
        return appConst;
    }

    @NonNull
    @Override
    public Application getApplication() {
        if (appInitInterface == null) {
            throw new IllegalStateException("uninitialized!Please complete the initialization first.");
        }
        return appInitInterface.getApplication();
    }

    @Override
    public boolean isDebug() {
        if (appInitInterface == null) {
            throw new IllegalStateException("uninitialized!Please complete the initialization first.");
        }
        return appInitInterface.isDebug();
    }

    @NonNull
    @Override
    public String getDBFilePath() {
        if (appInitInterface == null) {
            throw new IllegalStateException("uninitialized!Please complete the initialization first.");
        }
        return appInitInterface.getDBFilePath();
    }

    @NonNull
    @Override
    public RouteRule getRouteRule() {
        if (appInitInterface == null) {
            throw new IllegalStateException("uninitialized!Please complete the initialization first.");
        }
        return appInitInterface.getRouteRule();
    }

    @Override
    public String getAppTag() {
        if (appInitInterface == null) {
            throw new IllegalStateException("uninitialized!Please complete the initialization first.");
        }
        if(appInitInterface.getAppTag()==null){
            return "appLib";
        }else {
            return appInitInterface.getAppTag();
        }
    }

    public PathInfo getPathInfo(){
        if (pathInfo == null) {
            pathInfo = FileUtil.initPathInfo(instance().appInitInterface.getApplication());
        }
        return pathInfo;
    }
}
