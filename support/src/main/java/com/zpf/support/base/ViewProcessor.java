package com.zpf.support.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IEvent;
import com.zpf.api.IFunction1;
import com.zpf.api.ILayoutId;
import com.zpf.api.IManager;
import com.zpf.api.IPermissionResult;
import com.zpf.api.IconText;
import com.zpf.frame.IContainerHelper;
import com.zpf.frame.INavigator;
import com.zpf.frame.IRootLayout;
import com.zpf.frame.ITitleBar;
import com.zpf.frame.IViewLinker;
import com.zpf.support.constant.AppConst;
import com.zpf.support.model.SimpleEvent;
import com.zpf.support.model.IconTextEntry;
import com.zpf.support.model.TitleBarEntry;
import com.zpf.support.view.ContainerRootLayout;
import com.zpf.support.util.ContainerController;
import com.zpf.support.util.PermissionUtil;
import com.zpf.tool.SafeClickListener;
import com.zpf.frame.IViewProcessor;
import com.zpf.frame.IViewContainer;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.expand.util.EventManagerImpl;
import com.zpf.tool.permission.PermissionDescription;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 视图处理
 * Created by ZPF on 2018/6/14.
 */
public class ViewProcessor implements IViewProcessor, INavigator<Class<? extends IViewProcessor>> {
    protected final IViewContainer mContainer;
    protected final ITitleBar mTitleBar;
    protected final IRootLayout mRootLayout;
    protected final SafeClickListener safeClickListener = new SafeClickListener() {
        @Override
        public void click(View v) {
            ViewProcessor.this.onClick(v);
        }
    };

    public ViewProcessor() {
        this.mContainer = ContainerController.mInitingViewContainer;
        mRootLayout = onCreateRootLayout(getContext());
        mTitleBar = mRootLayout.getTitleBar();
        View layoutView = getLayoutView(getContext());
        if (layoutView != null) {
            mRootLayout.setContentView(layoutView);
        } else {
            int layoutId = 0;
            ILayoutId iLayoutId = getClass().getAnnotation(ILayoutId.class);
            if (iLayoutId != null) {
                layoutId = iLayoutId.value();
            }
            if (layoutId == 0) {
                layoutId = getLayoutId();
            }
            if (layoutId != 0) {
                mRootLayout.setContentView(layoutId);
            }
        }
    }

    protected IRootLayout onCreateRootLayout(Context context) {
        return new ContainerRootLayout(context);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        EventManagerImpl.get().unregister(getClass().getName());
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bindTitleBar(mRootLayout);
        GlobalConfigImpl.get().onObjectInit(this);
        EventManagerImpl.get().register(getClass().getName(), new IFunction1<IEvent<?>>() {
            @Override
            public void func(IEvent<?> iEvent) {
                handleEvent(iEvent);
            }
        });
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
    public void runWithPermission(final Runnable runnable, String... permissions) {
        mContainer.checkPermissions(new IPermissionResult() {
            @Override
            public void onPermissionChecked(boolean formResult, int requestCode,
                                            String[] requestPermissions, @Nullable List<String> missPermissions) {
                if (missPermissions == null || missPermissions.size() == 0) {
                    runnable.run();
                } else {
                    PermissionUtil.get().showPermissionRationaleDialog(
                            getCurrentActivity(), PermissionDescription.get().queryMissInfo(missPermissions));
                }
            }
        }, permissions);
    }


    public void runWithPermission(IPermissionResult result, String... permissions) {
        mContainer.checkPermissions(result, permissions);
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

    protected void bindAllChildren(View target) {
        ViewGroup parent;
        if (target instanceof ViewGroup) {
            parent = ((ViewGroup) target);
            View child;
            for (int i = 0; i < parent.getChildCount(); i++) {
                child = parent.getChildAt(i);
                if (child.isClickable() && child.getId() != View.NO_ID) {
                    child.setOnClickListener(safeClickListener);
                }
                if (child instanceof ViewGroup) {
                    bindAllChildren((ViewGroup) child);
                }
            }
        }
    }

    @Override
    public <T extends View> T find(@IdRes int viewId) {
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
    public boolean handleEvent(IEvent<?> event) {
        return !living();
    }

    @Override
    public void onReceiveLinker(IViewLinker linker) {

    }

    @Override
    public Activity getCurrentActivity() {
        return mContainer.getCurrentActivity();
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
            if (!TextUtils.isEmpty(titleBarEntry.leftLayoutAction)) {
                rootLayout.getTitleBar().getLeftLayout().setOnClickListener(new SafeClickListener() {
                    @Override
                    public void click(View v) {
                        handleEvent(new SimpleEvent<>(titleBarEntry.leftLayoutAction));
                    }
                });
            }
            if (!TextUtils.isEmpty(titleBarEntry.rightLayoutAction)) {
                rootLayout.getTitleBar().getRightLayout().setOnClickListener(new SafeClickListener() {
                    @Override
                    public void click(View v) {
                        handleEvent(new SimpleEvent<>(titleBarEntry.rightLayoutAction));
                    }
                });
            }
            if (titleBarEntry.hideStatusBar) {
                rootLayout.getStatusBar().setVisibility(View.GONE);
            } else {
                rootLayout.getStatusBar().setVisibility(View.VISIBLE);
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
        } else {
            if (mContainer instanceof Activity) {
                rootLayout.getTitleBar().getLayout().setVisibility(View.VISIBLE);
                rootLayout.getStatusBar().setVisibility(View.VISIBLE);
            } else {
                rootLayout.getTitleBar().getLayout().setVisibility(View.GONE);
                rootLayout.getStatusBar().setVisibility(View.GONE);
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
    public int getState() {
        return mContainer.getState();
    }

    @Override
    public boolean living() {
        return mContainer.living();
    }

    @Override
    public boolean interactive() {
        return mContainer.interactive();
    }

    @Override
    public boolean visible() {
        return mContainer.visible();
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
    public boolean addListener(Object listener, @Nullable Type listenerClass) {
        return mContainer.addListener(listener, listenerClass);
    }

    @Override
    public boolean removeListener(Object listener, @Nullable Type listenerClass) {
        return mContainer.removeListener(listener, listenerClass);
    }

    @Override
    public void push(Class<? extends IViewProcessor> target, Bundle params, int requestCode) {
        if (getContext() != null && living()) {
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
        if (living() && getContext() != null) {
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
        if (living() && getContext() != null) {
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
        if (living() && getContext() != null) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }
}
