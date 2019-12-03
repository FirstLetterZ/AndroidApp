package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IEvent;
import com.zpf.api.ILayoutId;
import com.zpf.api.IManager;
import com.zpf.api.IconText;
import com.zpf.frame.IContainerHelper;
import com.zpf.frame.INavigator;
import com.zpf.frame.IRootLayout;
import com.zpf.frame.ITitleBar;
import com.zpf.support.constant.AppConst;
import com.zpf.support.model.SimpleEvent;
import com.zpf.support.model.IconTextEntry;
import com.zpf.support.model.TitleBarEntry;
import com.zpf.support.view.RootLayout;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.PermissionUtil;
import com.zpf.tool.SafeClickListener;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewContainer;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.permission.OnLockPermissionRunnable;
import com.zpf.tool.permission.PermissionInfo;

import java.util.List;

/**
 * 视图处理
 * Created by ZPF on 2018/6/14.
 */
public class ViewProcessor<C> implements IViewProcessor<C>, INavigator<Class<? extends IViewProcessor>> {
    protected final IViewContainer mContainer;
    protected final ITitleBar mTitleBar;
    protected final IRootLayout mRootLayout;
    protected final SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ViewProcessor.this.onClick(v);
        }
    };
    protected C mLinker;//

    public ViewProcessor() {
        this.mContainer = ContainerController.mInitingViewContainer;
        mRootLayout = new RootLayout(getContext());
        mTitleBar = mRootLayout.getTitleBar();
        bindTitleBar(mRootLayout);
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
    }

    @Override
    @CallSuper
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
    public void onActivityChanged(boolean activity) {

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
            if (child.isClickable() && child.getId() != View.NO_ID) {
                child.setOnClickListener(safeClickListener);
            }
            if (child instanceof ViewGroup) {
                bindAllChildren((ViewGroup) child);
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
        return mContainer.getContext();
    }

    @Override
    public void onReceiveEvent(IEvent event) {

    }

    @Override
    public Activity getCurrentActivity() {
        return mContainer.getCurrentActivity();
    }

    @Override
    public void setLinker(C linker) {
        this.mLinker = linker;
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

    protected void bindTitleBar(IRootLayout rootLayout) {
        if (rootLayout == null) {
            return;
        }
        final TitleBarEntry titleBarEntry = getParams().getParcelable(AppConst.TITLE_ENTRY);
        if (titleBarEntry != null) {
            if (titleBarEntry.hideBottomShadow) {
                rootLayout.getShadowLine().getView().setVisibility(View.GONE);
            } else {
                rootLayout.getShadowLine().getView().setVisibility(View.VISIBLE);
            }
            if (titleBarEntry.hideStatusBar && titleBarEntry.hideTitleBar) {
                rootLayout.getTopLayout().getLayout().setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(titleBarEntry.leftLayoutAction)) {
                    rootLayout.getTitleBar().getLeftLayout().setOnClickListener(new SafeClickListener() {
                        @Override
                        public void click(View v) {
                            onReceiveEvent(new SimpleEvent(titleBarEntry.leftLayoutAction));
                        }
                    });
                }
                if (!TextUtils.isEmpty(titleBarEntry.rightLayoutAction)) {
                    rootLayout.getTitleBar().getRightLayout().setOnClickListener(new SafeClickListener() {
                        @Override
                        public void click(View v) {
                            onReceiveEvent(new SimpleEvent(titleBarEntry.rightLayoutAction));
                        }
                    });
                }
                if (titleBarEntry.hideStatusBar) {
                    rootLayout.getShadowLine().getView().setVisibility(View.GONE);
                } else {
                    rootLayout.getShadowLine().getView().setVisibility(View.VISIBLE);
                }
                if (titleBarEntry.hideTitleBar) {
                    rootLayout.getTitleBar().getLayout().setVisibility(View.GONE);
                } else {
                    rootLayout.getTitleBar().getLayout().setVisibility(View.VISIBLE);
                    if (titleBarEntry.leftIconEntry != null) {
                        bindIconText(rootLayout.getTitleBar().getLeftImage(), titleBarEntry.leftIconEntry);
                    }
                    if (titleBarEntry.leftTextEntry != null) {
                        bindIconText(rootLayout.getTitleBar().getLeftText(), titleBarEntry.leftTextEntry);
                    }
                    if (titleBarEntry.rightIconEntry != null) {
                        bindIconText(rootLayout.getTitleBar().getRightImage(), titleBarEntry.rightIconEntry);
                    }
                    if (titleBarEntry.rightTextEntry != null) {
                        bindIconText(rootLayout.getTitleBar().getRightText(), titleBarEntry.rightTextEntry);
                    }
                    if (titleBarEntry.titleEntry != null) {
                        bindIconText(rootLayout.getTitleBar().getTitle(), titleBarEntry.titleEntry);
                    }
                    if (titleBarEntry.subtitleEntry != null) {
                        bindIconText(rootLayout.getTitleBar().getSubTitle(), titleBarEntry.subtitleEntry);
                    }
                }
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
    public boolean close() {
        return mContainer.close();
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

    @Override
    public void push(Class<? extends IViewProcessor> target, Bundle params, int requestCode) {
        if (isLiving() && mContainer != null && getContext() != null) {
            if (mContainer.getNavigator() != null) {
                mContainer.getNavigator().push(target, params, requestCode);
            } else {
                Intent intent = new Intent();
                Context context = getContext();
                Class defContainerClass = null;
                IContainerHelper helper = GlobalConfigImpl.get().getGlobalInstance(IContainerHelper.class);
                if (helper != null) {
                    defContainerClass = helper.getDefContainerClassByType(mContainer.getContainerType());
                }
                if (defContainerClass == null) {
                    defContainerClass = CompatContainerActivity.class;
                }
                if (params == null) {
                    intent.setClass(context, defContainerClass);
                    intent.putExtra(AppConst.TARGET_VIEW_CLASS_NAME, target.getName());
                } else {
                    Class containerClass = null;
                    try {
                        containerClass = (Class) params.getSerializable(AppConst.TARGET_CONTAINER_CLASS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (containerClass == null) {
                        intent.setClass(context, defContainerClass);
                    } else {
                        intent.setClass(context, containerClass);
                    }
                    String containerAction = params.getString(AppConst.TARGET_CONTAINER_ACTION, null);
                    if (!TextUtils.isEmpty(containerAction)) {
                        intent.setAction(containerAction);

                    }
                    params.remove(AppConst.TARGET_CONTAINER_CLASS);
                    params.remove(AppConst.TARGET_CONTAINER_ACTION);
                    intent.putExtras(params);
                    intent.setExtrasClassLoader(params.getClassLoader());
                }
                intent.putExtra(AppConst.TARGET_VIEW_CLASS, target);
                mContainer.startActivityForResult(intent, requestCode);
            }
        }
    }

    @Override
    public void push(Class<? extends IViewProcessor> target, Bundle params) {
        this.push(target, params, AppConst.DEF_REQUEST_CODE);
    }

    @Override
    public void push(Class<? extends IViewProcessor> target) {
        this.push(target, null, AppConst.DEF_REQUEST_CODE);
    }

    @Override
    public void poll(int resultCode, Intent data) {
        if (isLiving() && mContainer != null && getContext() != null) {
            if (mContainer.getNavigator() != null) {
                mContainer.getNavigator().poll(resultCode, data);
            } else {
                mContainer.finishWithResult(resultCode, data);
            }
        }
    }

    @Override
    public void poll() {
        this.poll(AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void pollUntil(Class<? extends IViewProcessor> target, int resultCode, Intent data) {
        if (isLiving() && mContainer != null && getContext() != null) {
            if (mContainer.getNavigator() != null) {
                mContainer.getNavigator().pollUntil(target, resultCode, data);
            } else {
                //TODO
                mContainer.finishWithResult(resultCode, data);
            }
        }
    }

    @Override
    public void pollUntil(Class<? extends IViewProcessor> target) {
        this.pollUntil(target, AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void remove(Class<? extends IViewProcessor> target, int resultCode, Intent data) {
        if (isLiving() && mContainer != null && getContext() != null) {
            if (mContainer.getNavigator() != null) {
                mContainer.getNavigator().remove(target, resultCode, data);
            } else {
                //TODO
                mContainer.finishWithResult(resultCode, data);
            }
        }
    }

    @Override
    public void remove(Class<? extends IViewProcessor> target) {
        this.remove(target, AppConst.DEF_RESULT_CODE, null);
    }
}
