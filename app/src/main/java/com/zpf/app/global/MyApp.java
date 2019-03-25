package com.zpf.app.global;

import android.app.Application;
import android.graphics.Color;

import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.support.network.base.ResponseHandleInterface;
import com.zpf.support.view.RootLayout;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.GlobalConfigInterface;
import com.zpf.tool.expand.util.CacheMap;
import com.zpf.tool.expand.util.SpUtil;
import com.zpf.tool.gson.GsonUtil;

import java.util.UUID;

/**
 * Created by ZPF on 2019/3/25.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (PublicUtil.isPackageProces()) {
            CacheMap.setLocalStorage(SpUtil.get());
            GlobalConfigImpl.get().init(this, new GlobalConfigInterface() {
                @Override
                public UUID getId() {
                    return null;
                }

                @Override
                public void onObjectInit(Object object) {
                    if (object != null) {
                        if (object instanceof RootLayout) {
                            ((RootLayout) object).setTopViewBackground(Color.BLUE);
                        }
                    }

                }

                @Override
                public Object invokeMethod(Object object, String methodName, Object... args) {
                    return null;
                }

                @Override
                public <T> T getGlobalInstance(Class<T> target) {
                    if (target == JsonParserInterface.class) {
                        return (T) GsonUtil.get();
                    } else if (target == ResponseHandleInterface.class) {
                        return (T) ResponseHandleImpl.get();
                    }
                    return null;
                }
            });
        }
    }
}
