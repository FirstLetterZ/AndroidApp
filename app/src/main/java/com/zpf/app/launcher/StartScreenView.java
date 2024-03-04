package com.zpf.app.launcher;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zpf.app.R;
import com.zpf.support.base.ViewProcessor;
import com.zpf.tool.global.MainHandler;

public class StartScreenView extends ViewProcessor {

    @Override
    protected int getLayoutId() {
        return R.layout.layout_start;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mRootLayout.getStatusBar().setVisibility(View.GONE);
//        mTitleBar.getLayout().setVisibility(View.GONE);
//        loadLayout.setType(RefreshLayoutType.ONLY_PULL_DOWN);

    }

    @Override
    public void onResume() {
        super.onResume();
        MainHandler.get().postDelayed(new Runnable() {
            @Override
            public void run() {

//                params.putSerializable(AppConst.TARGET_CONTAINER_CLASS, CompatSinglePageActivity.class);

//                mNavigator.push(NetView.class, null);
//                mNavigator.remove(StartScreenView.class);
            }
        }, 2000);
    }
}
