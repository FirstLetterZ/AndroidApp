package com.zpf.support.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zpf.support.view.RootLayout;
import com.zpf.support.view.TitleBar;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.PermissionUtil;
import com.zpf.tool.SafeClickListener;
import com.zpf.tool.permission.OnLockPermissionRunnable;
import com.zpf.tool.permission.PermissionInfo;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewContainer;

import java.util.List;

/**
 * 视图处理
 * Created by ZPF on 2018/6/14.
 */
public abstract class ViewProcessor<C> implements IViewProcessor<C> {
    protected final IViewContainer mContainer;
    protected final TitleBar mTitleBar;
    protected final RootLayout mRootLayout;
    protected final SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ViewProcessor.this.onClick(v);
        }
    };
    protected C mConnector;//

    public ViewProcessor() {
        this.mContainer = ContainerController.mInitingViewContainer;
        mTitleBar = new TitleBar(mContainer.getContext());
        mRootLayout = new RootLayout(mTitleBar);
        View layoutView = getLayoutView(mContainer.getContext());
        if (layoutView == null) {
            mRootLayout.setContentView(null, getLayoutId());
        } else {
            mRootLayout.setContentView(layoutView);
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
    public boolean onInterceptBackPress() {
        return false;
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
        T result = mRootLayout.getLayout().findViewById(viewId);
        if (result != null) {
            result.setOnClickListener(clickListener);
        }
        return result;
    }

    public <T extends View> T $(@IdRes int viewId) {
        return mRootLayout.getLayout().findViewById(viewId);
    }

    public View getView() {
        return mRootLayout.getLayout();
    }

    @Override
    public void onReceiveEvent(String action, Object... params) {

    }

    @Override
    public void setConnector(C connector) {
        this.mConnector = connector;
    }

    @NonNull
    @Override
    public Bundle getParams() {
        return mContainer.getParams();
    }

    @Override
    public void navigate(Class cls, Bundle params, int requestCode) {

    }

    @Override
    public void navigate(Class cls, Bundle params) {

    }

    @Override
    public void navigate(Class cls) {

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

    protected View getLayoutView(Context context) {
        return null;
    }

    protected abstract int getLayoutId();

}
