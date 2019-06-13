package com.zpf.app.global;

import android.app.Application;

import com.zpf.tool.PublicUtil;
import com.zpf.tool.config.AppStackUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.expand.util.CacheMap;
import com.zpf.tool.expand.util.SpUtil;

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
            GlobalConfigImpl.get().init(this, new RealGlobalConfig());
        }
    }
}
