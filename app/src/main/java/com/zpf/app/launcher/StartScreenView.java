package com.zpf.app.launcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpf.app.activity.LoginView;
import com.zpf.frame.ILayoutId;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.tool.config.MainHandler;

@ILayoutId(R.layout.layout_start)
public class StartScreenView extends ViewProcessor {
    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        mRootLayout.getStatusBar().setVisibility(View.GONE);
        mTitleBar.getLayout().setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainHandler.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(LoginView.class);
                mContainer.finish();
            }
        }, 2000);
    }
}
