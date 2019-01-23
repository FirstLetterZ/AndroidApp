package com.zpf.tool.expand.util;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;

import com.zpf.api.GlobalConfigInterface;
import com.zpf.tool.AppContext;

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
            synchronized (RouteUtil.class) {
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
        ApplicationInfo info = application.getApplicationInfo();
        isDebug = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        hasInit = true;
    }

    public boolean isDebug() {
        if (hasInit) {
            throw new RuntimeException("GlobalConfigImpl is not initialized!");
        }
        return isDebug;
    }

    @Override
    public void objectInit(@NonNull Object object) {
        if (hasInit) {
            throw new RuntimeException("GlobalConfigImpl is not initialized!");
        }
        if (realGlobalConfig != null) {
            realGlobalConfig.objectInit(object);
        }
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object... args) {
        if (hasInit) {
            throw new RuntimeException("GlobalConfigImpl is not initialized!");
        }
        if (realGlobalConfig != null) {
            return realGlobalConfig.invokeMethod(object, methodName, args);
        } else {
            return null;
        }
    }

    @Override
    public <T> T getGlobalInstance(Class<T> target) {
        if (hasInit) {
            throw new RuntimeException("GlobalConfigImpl is not initialized!");
        }
        if (realGlobalConfig != null) {
            return realGlobalConfig.getGlobalInstance(target);
        } else {
            return null;
        }
    }
}
