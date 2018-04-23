package com.zpf.appLib.util;

import android.view.View;

/**
 * Created by ZPF on 2018/4/16.
 */
public abstract class SafeClickListener implements View.OnClickListener {
    private long lastClick = 0;
    private long timeInterval = 200;

    public SafeClickListener() {
    }

    public SafeClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public void onClick(View v) {
        //防止快速点击
        if (System.currentTimeMillis() - lastClick > timeInterval) {
            lastClick = System.currentTimeMillis();
            click(v);
        }
    }

    public abstract void click(View v);
}
