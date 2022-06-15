package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.IManager;
import com.zpf.api.IReceiver;
import com.zpf.api.ITransRecord;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewLinker;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewState;
import com.zpf.support.constant.AppConst;
import com.zpf.support.model.IconTextEntry;
import com.zpf.support.model.TitleBarEntry;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.PermissionUtil;
import com.zpf.support.view.PhonePageLayout;
import com.zpf.tool.SafeClickListener;
import com.zpf.tool.expand.event.EventManager;
import com.zpf.tool.expand.event.IEvent;
import com.zpf.tool.expand.event.SimpleEvent;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.permission.PermissionDescription;
import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;
import com.zpf.views.type.ITopBar;
import com.zpf.views.type.IconText;
import com.zpf.views.window.ICustomWindowManager;

import java.util.LinkedList;
import java.util.List;

/**
 * 视图处理
 * Created by ZPF on 2018/6/14.
 */
public class ViewProcessor implements IViewProcessor {
    protected final IViewContainer mContainer;
    @Nullable
    protected final ITopBar mTopBar;
    protected final PhonePageLayout mRootLayout;
    protected final SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ViewProcessor.this.onClick(v);
        }
    };
    private LinkedList<Runnable> waitRunList;

    protected final IReceiver<IEvent> receiver = new IReceiver<IEvent>() {

        @NonNull
        @Override
        public String name() {
            return getClass().getName();
        }

        @Override
        public void onReceive(@NonNull IEvent event, @NonNull ITransRecord record) {
            if (mContainer.getState().isLiving()) {
                handleEvent(event, record);
            }
        }
    };

    public ViewProcessor() {
        this.mContainer = ContainerController.mInitingViewContainer;
        mRootLayout = onCreateRootLayout(getContext());
        mTopBar = mRootLayout.getTopBar();
        View layoutView = getLayoutView(getContext());
        if (layoutView != null) {
            mRootLayout.setContentView(layoutView);
        } else {
            int layoutId = getLayoutId();
            if (layoutId != 0) {
                mRootLayout.setContentView(layoutId);
            }
        }
        if (mTopBar != null && hideTopBar()) {
            mTopBar.getView().setVisibility(View.GONE);
        }
    }

    protected boolean hideTopBar() {
        return !(mContainer instanceof Activity);
    }

    protected PhonePageLayout onCreateRootLayout(Context context) {
        return new PhonePageLayout(context);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        EventManager.get().unregister(receiver.name());
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindTitleBar(mRootLayout);
        CentralManager.onObjectInit(this);
        EventManager.get().register(receiver);
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
    @CallSuper
    public void onVisibleChanged(boolean visibility) {
        if (visibility && waitRunList != null) {
            for (Runnable runnable : waitRunList) {
                runnable.run();
            }
            waitRunList.clear();
        }
    }

    @Override
    public boolean onInterceptBackPress() {
        return false;
    }

    @Override
    public void runWithPermission(final Runnable runnable, String... permissions) {
        PermissionManager.get().checkPermission(mContainer, 0, new IPermissionResultListener() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
                if (missPermissions == null || missPermissions.size() == 0) {
                    runnable.run();
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            getCurrentActivity(), PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, permissions);
    }

    @Override
    public void runAfterVisible(Runnable runnable, boolean nextTime) {
        if (runnable == null) {
            return;
        }
        if (nextTime || !mContainer.getState().isVisible()) {
            LinkedList<Runnable> list = waitRunList;
            if (list == null) {
                list = new LinkedList<>();
                waitRunList = list;
            }
            list.add(runnable);
        } else {
            runnable.run();
        }
    }

    public <T extends View> T bind(@IdRes int viewId) {
        return bind(viewId, safeClickListener);
    }

    @Override
    public <T extends View> T bind(@IdRes int viewId, View.OnClickListener clickListener) {
        T result = mRootLayout.getRootView().findViewById(viewId);
        if (result != null) {
            result.setOnClickListener(clickListener);
        }
        return result;
    }

    protected void bindAllChildren(View target, @Nullable Class<?> viewType) {
        ViewGroup parent;
        if (target instanceof ViewGroup) {
            parent = ((ViewGroup) target);
            View child;
            for (int i = 0; i < parent.getChildCount(); i++) {
                child = parent.getChildAt(i);
                if (child.isClickable() && child.getId() != View.NO_ID &&
                        (viewType == null || viewType.isAssignableFrom(child.getClass()))) {
                    child.setOnClickListener(safeClickListener);
                }
                if (child instanceof ViewGroup) {
                    bindAllChildren((ViewGroup) child, viewType);
                }
            }
        }
    }

    @Override
    public <T extends View> T find(@IdRes int viewId) {
        return mRootLayout.getRootView().findViewById(viewId);
    }

    @Override
    public View getView() {
        return mRootLayout.getRootView();
    }

    @Override
    public Context getContext() {
        return mContainer.getContext();
    }

    protected void handleEvent(IEvent event, ITransRecord record) {

    }

    @Override
    public void onReceiveLinker(IViewLinker linker) {

    }

    @Override
    public Activity getCurrentActivity() {
        return mContainer.getCurrentActivity();
    }

    @Override
    public IViewState getState() {
        return mContainer.getState();
    }

    @Override
    public boolean initWindow(@NonNull Window window) {
        return false;
    }

    @NonNull
    @Override
    public Bundle getParams() {
        return mContainer.getParams();
    }

    @Override
    public ICustomWindowManager getCustomWindowManager() {
        return mContainer.getCustomWindowManager();
    }

    protected void bindTitleBar(PhonePageLayout rootLayout) {
        if (rootLayout == null) {
            return;
        }
        final ITopBar topBar = rootLayout.getTopBar();
        if (topBar == null) {
            return;
        }
        final TitleBarEntry titleBarEntry = getParams().getParcelable(AppConst.TITLE_ENTRY);
        if (titleBarEntry != null) {
            if (!TextUtils.isEmpty(titleBarEntry.leftLayoutAction)) {
                topBar.getLeftLayout().setOnClickListener(new SafeClickListener() {
                    @Override
                    public void click(View v) {
                        handleEvent(new SimpleEvent(-1, null, titleBarEntry.leftLayoutAction), null);
                    }
                });
            }
            if (!TextUtils.isEmpty(titleBarEntry.rightLayoutAction)) {
                topBar.getRightLayout().setOnClickListener(new SafeClickListener() {
                    @Override
                    public void click(View v) {
                        handleEvent(new SimpleEvent(-1, null, titleBarEntry.rightLayoutAction), null);
                    }
                });
            }
            if (titleBarEntry.hideStatusBar) {
                topBar.getStatusBar().setVisibility(View.GONE);
            } else {
                topBar.getStatusBar().setVisibility(View.VISIBLE);
            }
            if (titleBarEntry.hideTitleBar) {
                topBar.getView().setVisibility(View.GONE);
            } else {
                topBar.getView().setVisibility(View.VISIBLE);
                if (titleBarEntry.leftIconEntry != null) {
                    bindIconText(topBar.getLeftImage(), titleBarEntry.leftIconEntry);
                }
                if (titleBarEntry.leftTextEntry != null) {
                    bindIconText(topBar.getLeftText(), titleBarEntry.leftTextEntry);
                }
                if (titleBarEntry.rightIconEntry != null) {
                    bindIconText(topBar.getRightImage(), titleBarEntry.rightIconEntry);
                }
                if (titleBarEntry.rightTextEntry != null) {
                    bindIconText(topBar.getRightText(), titleBarEntry.rightTextEntry);
                }
                if (titleBarEntry.titleEntry != null) {
                    bindIconText(topBar.getTitle(), titleBarEntry.titleEntry);
                }
                if (titleBarEntry.subtitleEntry != null) {
                    bindIconText(topBar.getSubTitle(), titleBarEntry.subtitleEntry);
                }
            }
        } else {
            if (mContainer instanceof Activity) {
                topBar.getView().setVisibility(View.VISIBLE);
                topBar.getStatusBar().setVisibility(View.VISIBLE);
            } else {
                topBar.getView().setVisibility(View.GONE);
            }
        }
    }

    protected void bindIconText(IconText iconText, IconTextEntry entry) {
        if (iconText == null || entry == null) {
            return;
        }
        if (entry.getTextSize() != 0) {
            iconText.setTextSize(entry.getTextSize());
        }
        if (entry.getTextColor() != 0) {
            iconText.setTextColor(entry.getTextColor());
        }
        if (entry.getIconResId() != 0) {
            iconText.setIconFont(entry.getIconResId());
        } else if (entry.getImageResId() != 0) {
            iconText.setImageOnly(entry.getImageResId());
        } else if (entry.getTextResId() != 0) {
            iconText.setText(entry.getTextResId());
        } else if (!TextUtils.isEmpty(entry.getTextString())) {
            iconText.setText(entry.getTextString());
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
        View v = find(viewId);
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
    public boolean close() {
        return mContainer.close();
    }

    @Override
    public IManager<ICancelable> getCancelableManager() {
        return mContainer.getCancelableManager();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }
}
