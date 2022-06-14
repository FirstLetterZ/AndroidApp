package com.zpf.support.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IDataCallback;
import com.zpf.api.INavigator;
import com.zpf.api.OnActivityResultListener;
import com.zpf.frame.IContainerHelper;
import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.constant.AppConst;
import com.zpf.tool.global.CentralManager;
import com.zpf.tool.global.MainHandler;
import com.zpf.tool.stack.AppStackUtil;

import java.lang.ref.WeakReference;

/**
 * @author Created by ZPF on 2021/4/2.
 */
public class ContainerNavigator implements INavigator<Class<? extends IViewProcessor>, Intent, Intent> {
    private WeakReference<IViewContainer> mContainer;

    public void setContainer(IViewContainer container) {
        this.mContainer = new WeakReference<>(container);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target) {
        this.push(target, null, null);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params) {
        this.push(target, params, null);
    }

    @Override
    public void push(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params, @Nullable final IDataCallback<Intent> callback) {
        IViewContainer viewContainer = getLivingContainer();
        if (viewContainer == null) {
            return;
        }
        Intent intent = new Intent();
        Class<?> containerClass = null;
        Context context = viewContainer.getContext();
        if (params == null) {
            IContainerHelper helper = CentralManager.getInstance(IContainerHelper.class);
            if (helper != null) {
                containerClass = helper.getDefContainerClassByType(viewContainer.getContainerType());
            }
        } else {
            intent = params;
            try {
                containerClass = (Class<?>) params.getSerializableExtra(AppConst.TARGET_CONTAINER_CLASS);
                params.removeExtra(AppConst.TARGET_CONTAINER_CLASS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.replaceExtras(params);
        }
        if (containerClass == null) {
            containerClass = CompatContainerActivity.class;
        }
        intent.setClass(context, containerClass);
        intent.putExtra(AppStackUtil.STACK_ITEM_NAME, target.getName());
        intent.putExtra(AppConst.TARGET_VIEW_CLASS_NAME, target.getName());
        intent.putExtra(AppConst.TARGET_VIEW_CLASS, target);
        if (callback == null) {
            viewContainer.startActivityForResult(intent, AppConst.DEF_REQUEST_CODE);
        } else {
            viewContainer.startActivityForResult(intent, new OnActivityResultListener() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                    callback.onResult(resultCode, data);
                }
            });
        }
    }

    @Override
    public void replace(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params) {
        this.pop();
        push(target, params);
    }

    @Override
    public void pop() {
        this.pop(AppConst.DEF_RESULT_CODE, null);
    }

    @Override
    public void pop(int resultCode, @Nullable Intent data) {
        final IViewContainer viewContainer = getLivingContainer();
        Activity activity;
        if (viewContainer == null || (activity = viewContainer.getCurrentActivity()) == null) {
            return;
        }
        activity.setResult(resultCode, data);
        activity.finish();
    }

    @Override
    public void popToRoot(@Nullable Intent data) {
        AppStackUtil.finishToRoot();
        if (data == null) {
            return;
        }
        postData(data, "");
    }

    @Override
    public boolean popTo(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent data) {
        final String targetName = target.getName();
        if (!AppStackUtil.finishUntil(targetName)) {
            push(target);
        }
        postData(data, targetName);
        return true;
    }

    @Override
    public boolean remove(@NonNull Class<? extends IViewProcessor> target) {
        return AppStackUtil.finishByName(target.getName());
    }

    private void postData(final Intent data, final String targetName) {
        if (data == null || targetName == null) {
            return;
        }
        MainHandler.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = AppStackUtil.getTopActivity();
                if (activity instanceof OnActivityResultListener) {
                    String itemName = activity.getIntent().getStringExtra(AppStackUtil.STACK_ITEM_NAME);
                    if (targetName.length() == 0 || targetName.equals(itemName)) {
                        ((OnActivityResultListener) activity).onActivityResult(AppConst.POLL_BACK_REQUEST_CODE, AppConst.POLL_BACK_RESULT_CODE, data);
                    }
                }
            }
        }, 100);
    }

    private IViewContainer getLivingContainer() {
        if (mContainer == null) {
            return null;
        }
        final IViewContainer viewContainer = mContainer.get();
        if (viewContainer == null || !viewContainer.getState().isLiving()) {
            return null;
        }
        return viewContainer;
    }
}