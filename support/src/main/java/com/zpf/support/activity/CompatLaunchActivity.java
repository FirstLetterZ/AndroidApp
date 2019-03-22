package com.zpf.support.activity;

import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.R;

import java.lang.reflect.Constructor;

/**
 * Created by ZPF on 2019/3/22.
 */
public class CompatLaunchActivity extends CompatContainerActivity {

    @Override
    protected IViewProcessor unspecifiedViewProcessor() {
        IViewProcessor viewProcessor = null;
        String launcherViewName = getApplicationInfo().metaData.getString(getString(R.string.default_launcher));
        if (launcherViewName != null && launcherViewName.length() > 0) {
            try {
                Class targetViewClass = Class.forName(launcherViewName);
                if (targetViewClass != null) {
                    Constructor<IViewProcessor> constructor = targetViewClass.getConstructor();
                    if (constructor != null) {
                        viewProcessor = constructor.newInstance();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return viewProcessor;
    }
}
