package com.zpf.app.launcher;

import android.content.res.Configuration;

import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.util.LogUtil;

/**
 * Created by ZPF on 2019/3/25.
 */

public class StartScreenActivity extends CompatContainerActivity {
    @Override
    protected IViewProcessor unspecifiedViewProcessor() {
        LogUtil.setLogOut(true);
        return new StartScreenView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
