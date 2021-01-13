package com.zpf.app.launcher;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.zpf.api.ILayoutId;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;

@ILayoutId(value = R.layout.layout_start)
public class StartScreenView extends ViewProcessor {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootLayout.getStatusBar().setVisibility(View.GONE);
        mTitleBar.getLayout().setVisibility(View.GONE);
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
