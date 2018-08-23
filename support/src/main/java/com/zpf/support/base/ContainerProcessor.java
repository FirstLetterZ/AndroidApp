package com.zpf.support.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpf.support.constant.BaseKeyConst;
import com.zpf.support.generalUtil.SafeClickListener;
import com.zpf.support.interfaces.OnLackOfPermissions;
import com.zpf.support.interfaces.TitleBarInterface;
import com.zpf.support.interfaces.ViewContainerInterface;
import com.zpf.support.interfaces.ContainerProcessorInterface;
import com.zpf.support.util.CacheMap;
import com.zpf.support.util.LifecycleLogUtil;

/**
 * Created by ZPF on 2018/6/14.
 */
public abstract class ContainerProcessor implements ContainerProcessorInterface {
    protected ViewContainerInterface mContainer;
    protected TitleBarInterface mTitleBar;
    private SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ContainerProcessor.this.onClick(v);
        }
    };

    public ContainerProcessor(ViewContainerInterface container) {
        this.mContainer = container;
        this.mTitleBar = container.getRootLayout().getTitleBar();
        if (CacheMap.getBoolean(BaseKeyConst.IS_DEBUG)) {
            new LifecycleLogUtil(container);
        }
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

    @Override
    public void runWithPermission(Runnable runnable, String... permissions) {
        mContainer.checkPermissions(runnable, null, permissions);
    }

    @Override
    public void runWithPermission(Runnable runnable, OnLackOfPermissions onLackOfPermissions, String... permissions) {
        mContainer.checkPermissions(runnable, onLackOfPermissions, permissions);
    }

    public <T extends View> T $(int viewId) {
        if (mContainer != null) {
            return mContainer.getRootLayout().getLayout().findViewById(viewId);
        } else {
            return null;
        }
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
