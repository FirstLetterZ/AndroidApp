package com.zpf.support.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpf.support.data.constant.AppContext;
import com.zpf.support.data.constant.LifecycleState;
import com.zpf.support.interfaces.TitleBarInterface;
import com.zpf.support.interfaces.ViewContainerInterface;
import com.zpf.support.interfaces.ViewInterface;
import com.zpf.support.util.SafeClickListener;

/**
 * Created by ZPF on 2018/6/14.
 */
public abstract class BaseView implements ViewInterface {
    protected ViewContainerInterface mContainer;
    protected TitleBarInterface mTitleBar;
    private SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            BaseView.this.onClick(v);
        }
    };

    public BaseView(ViewContainerInterface container) {
        this.mContainer = container;
        this.mTitleBar = container.getRootLayout().getTitleBar();
    }

    @Override
    public void onPreCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        this.mContainer = null;
        this.mTitleBar = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onNewIntent(@NonNull Intent intent) {

    }

    @Override
    public void onVisibleChanged(boolean visibility) {

    }

    public boolean isAfterState(@LifecycleState int state) {
        int lifeState;
        if (mContainer == null) {
            lifeState = LifecycleState.AFTER_DESTROY;
        } else {
            lifeState = mContainer.getState();
        }
        return lifeState >= state;
    }

    public Context getContext() {
        return mContainer != null ? mContainer.getContext() : AppContext.get();
    }

    public <T extends View> T $(int viewId) {
        return mContainer.getRootLayout().getLayout().findViewById(viewId);
    }

    public void setText(int viewId, CharSequence content) {
        TextView textView = $(viewId);
        if (textView != null) {
            textView.setText(content);
            textView.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        }
    }

    public void setClickListener(View... views) {
        if (views != null) {
            for (View view : views) {
                setClickListener(view);
            }
        }
    }

    public void setClickListener(View v) {
        if (v != null) {
            v.setOnClickListener(safeClickListener);
        }
    }

    public void setClickListener(int viewId) {
        View v = $(viewId);
        if (v != null) {
            v.setOnClickListener(safeClickListener);
        }
    }

    public void onClick(View view) {

    }

}
