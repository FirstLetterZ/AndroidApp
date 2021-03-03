package com.zpf.app.launcher;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.zpf.api.ILayoutId;
import com.zpf.refresh.util.OnRefreshListener;
import com.zpf.refresh.util.RefreshLayoutType;
import com.zpf.refresh.view.LoadLayout;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.tool.config.MainHandler;

@ILayoutId(value = R.layout.layout_start)
public class StartScreenView extends ViewProcessor {
    private LoadLayout loadLayout = find(R.id.ll_load);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootLayout.getStatusBar().setVisibility(View.GONE);
        mTitleBar.getLayout().setVisibility(View.GONE);
        loadLayout.setType(RefreshLayoutType.BOTH_UP_DOWN);
        loadLayout.setListener(new OnRefreshListener() {
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
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        MainHandler.get().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Bundle params = new Bundle();
////                params.putSerializable(AppConst.TARGET_CONTAINER_CLASS, CompatSinglePageActivity.class);
//                push(LoginView.class, params);
//                remove(StartScreenView.class);
//            }
//        }, 2000);
//    }
}
