package com.zpf.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zpf.api.ILayoutId;
import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.util.LogUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
@ILayoutId(R.layout.activity_main)
public class LoginView extends ViewProcessor {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleBar.getTitle().setText("测试");
        bindAllChildren(mRootLayout.getContentLayout());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("test", "1234");
    }

    @Override
    public void onClick(View view) {
        LogUtil.e(view.getClass().getName());
        switch (view.getId()) {
            case R.id.btn_start:
                break;
            case R.id.btn_cancel:
                push(TestView.class);
                break;
        }
    }
}
