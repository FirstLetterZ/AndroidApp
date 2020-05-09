package com.zpf.app.global;

import android.app.Application;

import com.zpf.app.plugin.MainClassLoader;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.stack.AppStackUtil;
import com.zpf.tool.expand.util.CacheMap;
import com.zpf.tool.expand.util.ClassLoaderImpl;
import com.zpf.tool.expand.util.SpUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (PublicUtil.isPackageProces(this)) {
            registerActivityLifecycleCallbacks(AppStackUtil.get());
            CacheMap.setLocalStorage(SpUtil.get());
            ClassLoaderImpl.get().add(new MainClassLoader());
            GlobalConfigImpl.get().init(this, new RealGlobalConfig());
            LogUtil.setLogOut(GlobalConfigImpl.get().isDebug());
        }
    }
}
