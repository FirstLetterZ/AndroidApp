package com.zpf.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpf.app.config.RouteRuleList;
import com.zpf.app.layout.WelcomeView;
import com.zpf.appLib.base.BaseActivity;
import com.zpf.appLib.util.RouteUtil;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            pushActivity(WelcomeView.class);
        }
        RouteUtil.instance().init(new RouteRuleList());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        pushActivity(WelcomeView.class);
    }

    @Override
    protected boolean isRootActivity() {
        return true;
    }

}
