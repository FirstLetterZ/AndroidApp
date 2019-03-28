package com.zpf.app.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.frame.ILayoutId;

import com.zpf.support.base.ViewProcessor;
import com.zpf.app.R;
import com.zpf.support.util.LogUtil;

/**
 * Created by ZPF on 2019/3/25.
 */
@ILayoutId(R.layout.activity_main)
public class LoginView extends ViewProcessor {

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        mTitleBar.getTitle().setText("测试");
        bindAllChildren(mRootLayout.getContentLayout());
    }

    @Override
    public void onClick(View view) {
        LogUtil.e(view.getClass().getName());
        switch (view.getId()) {
            case R.id.btn_start:
                break;
            case R.id.btn_cancel:
                navigate(TestView.class);
                break;
        }
    }

    private void bindAllChildren(ViewGroup viewGroup) {
        View child;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                bindAllChildren((ViewGroup) child);
            } else if (child.isClickable() && child.getId() != View.NO_ID) {
                child.setOnClickListener(safeClickListener);
            }
        }
    }
}
