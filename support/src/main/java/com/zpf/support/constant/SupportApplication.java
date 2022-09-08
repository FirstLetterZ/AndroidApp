package com.zpf.support.constant;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.CallSuper;

import com.zpf.tool.PublicUtil;

public abstract class SupportApplication extends Application {
    @Override
    @CallSuper
    public void onCreate() {
        super.onCreate();
        String name = PublicUtil.getProcessName(this);
        if (getPackageName().equals(name)) {
            initBaseConfig();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initOnChildThread();
                }
            }).start();
            initOnMainThread();
        } else {
            initChildProcess(name);
        }
    }

    protected abstract void initBaseConfig();

    protected abstract void initOnMainThread();

    protected abstract void initOnChildThread();

    protected abstract void initChildProcess(String processName);

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (!AppConst.FontScaleWithSystem) {
            Configuration configuration = resources.getConfiguration();
            if (configuration.fontScale != 1f) {
                configuration.fontScale = 1f;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            }
        }
        return resources;
    }
}
