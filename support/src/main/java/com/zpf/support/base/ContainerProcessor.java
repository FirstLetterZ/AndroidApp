package com.zpf.support.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpf.tool.SafeClickListener;
import com.zpf.tool.expand.util.GlobalConfigImpl;
import com.zpf.tool.permission.OnLockPermissionRunnable;
import com.zpf.tool.permission.PermissionInfo;
import com.zpf.api.ContainerProcessorInterface;
import com.zpf.api.TitleBarInterface;
import com.zpf.api.ViewContainerInterface;
import com.zpf.support.util.LifecycleLogUtil;
import com.zpf.support.util.PermissionUtil;

import java.util.List;

/**
 * Created by ZPF on 2018/6/14.
 */
public abstract class ContainerProcessor implements ContainerProcessorInterface {
    protected ViewContainerInterface mContainer;
    protected TitleBarInterface mTitleBar;
    protected final SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ContainerProcessor.this.onClick(v);
        }
    };

    public ContainerProcessor(ViewContainerInterface container) {
        this.mContainer = container;
        this.mTitleBar = container.getRootLayout().getTitleBar();
        if (GlobalConfigImpl.get().isDebug()) {
            new LifecycleLogUtil(container);
        }
    }

    @Override
    public void onPreCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onRestart() {

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
        mContainer.checkPermissions(runnable, new OnLockPermissionRunnable() {
            @Override
            public void onLock(List<PermissionInfo> list) {
                PermissionUtil.get().showPermissionRationaleDialog(mContainer.getCurrentActivity(), list);
            }
        }, permissions);
    }

    @Override
    public void runWithPermission(Runnable runnable, Runnable onLackOfPermissions, String... permissions) {
        mContainer.checkPermissions(runnable, onLackOfPermissions, permissions);
    }

    public void runWithPermission(Runnable runnable, OnLockPermissionRunnable onLackOfPermissions, String... permissions) {
        mContainer.checkPermissions(runnable, onLackOfPermissions, permissions);
    }

    public <T extends View> T bind(@IdRes int viewId) {
        return bind(viewId, safeClickListener);
    }

    @Override
    public <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener) {
        T result = null;
        if (mContainer != null) {
            result = mContainer.getRootLayout().getLayout().findViewById(viewId);
            if (result != null) {
                result.setOnClickListener(clickListener);
            }
        }
        return result;
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
