package com.zpf.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zpf.frame.ILayoutId;

import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.util.LogUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
@ILayoutId(R.layout.activity_main)
public class LoginView extends ViewProcessor {
    private View btnStart = bind(R.id.btn_start);

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        mRootLayout.setTopViewBackground(Color.BLUE);
        mTitleBar.getTitle().setText("测试");
    }

    @Override
    public void onClick(View view) {
        LogUtil.e(view.getClass().getName());
        switch (view.getId()) {
            case R.id.btn_start:
                mContainer.startActivity(new Intent(getContext(), TestActivity.class));
                break;
        }
    }
}
