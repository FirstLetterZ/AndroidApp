package com.zpf.app.global;

import android.app.Application;

import com.zpf.tool.PublicUtil;
import com.zpf.tool.expand.cache.CacheMap;
import com.zpf.tool.expand.cache.SpUtil;
import com.zpf.tool.expand.util.Logger;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.stack.AppStackUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (PublicUtil.isPackageProcess(this)) {
            CentralManager.init(this, new RealGlobalConfig());
            AppStackUtil.init(this);
            CacheMap.setLocalStorageManager(SpUtil.get());
            Logger.setLogOut(CentralManager.debuggable());
        }
    }
}
