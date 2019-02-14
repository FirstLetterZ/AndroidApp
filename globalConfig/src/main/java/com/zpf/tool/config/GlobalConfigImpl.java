package com.zpf.tool.config;

import android.app.Application;
import android.content.pm.ApplicationInfo;

/**
 * Created by ZPF on 2018/10/12.
 */
public class GlobalConfigImpl implements GlobalConfigInterface {
    private GlobalConfigInterface realGlobalConfig;
    private boolean isDebug = true;
    private boolean hasInit = false;
    private static volatile GlobalConfigImpl mInstance;

    private GlobalConfigImpl() {
    }

    public static GlobalConfigImpl get() {
        if (mInstance == null) {
            synchronized (GlobalConfigImpl.class) {
                if (mInstance == null) {
                    mInstance = new GlobalConfigImpl();
                }
            }
        }
        return mInstance;
    }

    public void init(Application application, GlobalConfigInterface globalConfig) {
        AppContext.init(application);
        realGlobalConfig = globalConfig;
        isDebug = (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        hasInit = true;
    }

    @Override
    public boolean isDebug() {
        return hasInit && isDebug;
    }

    @Override
    public void onObjectInit(Object object) {
        if (realGlobalConfig != null) {
            realGlobalConfig.onObjectInit(object);
        }
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object... args) {
        if (realGlobalConfig != null) {
            return realGlobalConfig.invokeMethod(object, methodName, args);
        } else {
            return null;
        }
    }

    @Override
    public <T> T getGlobalInstance(Class<T> target) {
        if (realGlobalConfig != null) {
            return realGlobalConfig.getGlobalInstance(target);
        } else {
            return null;
        }
    }

}
