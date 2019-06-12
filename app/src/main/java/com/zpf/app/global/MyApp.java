package com.zpf.app.global;

import android.app.Application;
import android.graphics.Color;

import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.support.network.base.IResponseHandler;
import com.zpf.support.view.RootLayout;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.config.AppStackUtil;
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
            registerActivityLifecycleCallbacks(AppStackUtil.get());
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
                            ((RootLayout) object).getTopLayout().getLayout().setBackgroundColor(Color.BLUE);
                            ((RootLayout) object).getShadowLine().setShadowColor(Color.DKGRAY);
                            ((RootLayout) object).getShadowLine().setElevation(4);
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
                        return (T) GsonUtil.mInstance;
                    } else if (target == IResponseHandler.class) {
                        return (T) ResponseHandleImpl.get();
                    }
                    return null;
                }
            });
        }
    }
}
