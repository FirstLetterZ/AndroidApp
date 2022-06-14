package com.zpf.app.launcher;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.zpf.app.R;
import com.zpf.support.base.ViewProcessor;
import com.zpf.tool.global.MainHandler;
import com.zpf.views.bounce.BounceLayout;
import com.zpf.views.bounce.OnLoadListener;

public class StartScreenView extends ViewProcessor {
    private BounceLayout loadLayout = find(R.id.ll_load);

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
        loadLayout.setListener(new OnLoadListener() {
            @Override
            public void onRefresh() {
                MainHandler.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadLayout.setResult(true);
                    }
                }, 5000);
            }

            @Override
            public void onLoadMore() {
                MainHandler.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadLayout.setResult(false);
                    }
                }, 5000);
            }
        });

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
