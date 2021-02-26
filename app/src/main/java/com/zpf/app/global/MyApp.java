package com.zpf.app.global;

import android.app.Application;

import com.zpf.api.IClassLoader;
import com.zpf.app.plugin.MainClassLoader;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.expand.cache.CacheMap;
import com.zpf.tool.expand.cache.SpUtil;
import com.zpf.tool.expand.util.ClassLoaderImpl;
import com.zpf.tool.expand.util.LogUtil;
import com.zpf.tool.stack.AppStackUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (PublicUtil.isPackageProcess(this)) {
            registerActivityLifecycleCallbacks(AppStackUtil.get());
            CacheMap.setLocalStorageManager(SpUtil.get());
            ClassLoaderImpl.get().add(new MainClassLoader());
            GlobalConfigImpl.get().init(this, new RealGlobalConfig());
            LogUtil.setLogOut(GlobalConfigImpl.get().isDebug());
            GlobalConfigImpl.get().getGlobalInstance(IClassLoader.class).getClass("aaa");
        }
    }
}
