package com.zpf.app.global;

import android.graphics.Color;

import com.zpf.api.IClassLoader;
import com.zpf.api.dataparser.JsonParserInterface;
import com.zpf.support.network.base.IResponseHandler;
import com.zpf.support.view.LinearRootLayout;
import com.zpf.tool.config.GlobalConfigInterface;
import com.zpf.tool.expand.util.ClassLoaderImpl;
import com.zpf.tool.gson.GsonUtil;

import java.util.UUID;

/**
 * Created by ZPF on 2019/6/13.
 */
public class RealGlobalConfig implements GlobalConfigInterface {
    private UUID uuid = UUID.randomUUID();

    @Override
    public String getId() {
        return uuid.toString();
    }

    @Override
    public void onObjectInit(Object object) {
        if (object != null) {
            if (object instanceof LinearRootLayout) {
                ((LinearRootLayout) object).getLayout().setBackgroundColor(Color.WHITE);
                ((LinearRootLayout) object).getTopLayout().getLayout().setBackgroundColor(Color.BLUE);
                ((LinearRootLayout) object).getShadowLine().setShadowColor(Color.DKGRAY);
                ((LinearRootLayout) object).getShadowLine().setElevation(4);
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
        } else if (target == IResponseHandler.class) {
            return (T) ResponseHandleImpl.get();
        } else if (target == IClassLoader.class) {
            return (T) ClassLoaderImpl.get();
        }
        return null;
    }
}
