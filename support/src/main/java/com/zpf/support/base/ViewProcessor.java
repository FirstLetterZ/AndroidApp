package com.zpf.support.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.ILayoutId;
import com.zpf.api.IManager;
import com.zpf.api.OnActivityResultListener;
import com.zpf.frame.IRootLayout;
import com.zpf.frame.ITitleBar;
import com.zpf.support.view.RootLayout;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.PermissionUtil;
import com.zpf.tool.SafeClickListener;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewContainer;
import com.zpf.tool.permission.OnLockPermissionRunnable;
import com.zpf.tool.permission.PermissionInfo;

import java.util.List;

/**
 * 视图处理
 * Created by ZPF on 2018/6/14.
 */
public class ViewProcessor<C> implements IViewProcessor<C>, OnActivityResultListener {
    protected final IViewContainer mContainer;
    protected final ITitleBar mTitleBar;
    protected final IRootLayout mRootLayout;
    protected final SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ViewProcessor.this.onClick(v);
        }
    };
    protected C mWorker;//

    public ViewProcessor() {
        this.mContainer = ContainerController.mInitingViewContainer;
        mRootLayout = new RootLayout(getContext());
        mTitleBar = mRootLayout.getTitleBar();
        ILayoutId iLayoutId = getClass().getAnnotation(ILayoutId.class);
        if (iLayoutId != null && iLayoutId.value() != 0) {
            mRootLayout.setContentView(null, iLayoutId.value());
        } else {
            View layoutView = getLayoutView(mContainer.getContext());
            if (layoutView == null) {
                mRootLayout.setContentView(null, getLayoutId());
            } else {
                mRootLayout.setContentView(layoutView);
            }
        }
    }

    @Override
    public void onDestroy() {
        mContainer.unbindView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

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
    public void onParamChanged(Bundle newParams) {

    }

    @Override
    public void onVisibleChanged(boolean visibility) {

    }

    @Override
    public void onActiviityChanged(boolean activity) {

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

    protected void bindAllChildren(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
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

    @Override
    public <T extends View> T $(@IdRes int viewId) {
        return mRootLayout.getLayout().findViewById(viewId);
    }

    @Override
    public View getView() {
        return mRootLayout.getLayout();
    }

    @Override
    public Context getContext() {
        return mContainer != null ? mContainer.getContext() : null;
    }

    @Override
    public void onReceiveEvent(String action, Object... params) {

    }

    @Override
    public void setLinker(C worker) {
        this.mWorker = worker;
    }

    @NonNull
    @Override
    public Bundle getParams() {
        return mContainer.getParams();
    }

//    @Override
//    public void navigate(Class cls, Bundle params, int requestCode) {
//        Context context = mContainer.getContext();
//        if (context == null) {
//            return;
//        }
//        Intent intent = new Intent();
//        Class defContainerClass = CompatContainerActivity.class;
//        if (params == null) {
//            intent.setClass(context, defContainerClass);
//        } else {
//            String containerName = params.getString(AppConst.TARGET_CONTAINER_CLASS, null);
//            if (TextUtils.isEmpty(containerName)) {
//                intent.setClass(context, defContainerClass);
//            } else {
//                intent.setClassName(context, containerName);
//                params.remove(AppConst.TARGET_CONTAINER_CLASS);
//            }
//            String containerAction = params.getString(AppConst.TARGET_CONTAINER_ACTION, null);
//            if (!TextUtils.isEmpty(containerAction)) {
//                intent.setAction(containerAction);
//                params.remove(AppConst.TARGET_CONTAINER_ACTION);
//            }
//            intent.putExtras(params);
//        }
//        intent.putExtra(AppConst.TARGET_VIEW_CLASS, cls);
//        mContainer.startActivityForResult(intent, requestCode);
//
//    }
//
//    @Override
//    public void navigate(Class cls, Bundle params) {
//        navigate(cls, params, -1);
//    }
//
//    @Override
//    public void navigate(Class cls) {
//        navigate(cls, null, -1);
//
//    }

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

    protected int getLayoutId() {
        return 0;
    }

    @Override
    public int getState() {
        return mContainer.getState();
    }

    @Override
    public boolean isLiving() {
        return mContainer.isLiving();
    }

    @Override
    public boolean isActive() {
        return mContainer.isActive();
    }

    @Override
    public void show(ICustomWindow window) {
        mContainer.show(window);
    }

    @Override
    public boolean dismiss() {
        return mContainer.dismiss();
    }

    @Override
    public IManager<ICancelable> getCancelableManager() {
        return mContainer.getCancelableManager();
    }

    @Override
    public boolean addListener(Object listener) {
        return mContainer.addListener(listener);
    }

    @Override
    public boolean removeListener(Object listener) {
        return mContainer.removeListener(listener);
    }
}
