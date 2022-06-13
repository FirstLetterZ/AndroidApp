package com.zpf.support.model;

import android.app.Activity;
import android.app.Fragment;
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

/**
 * @author Created by ZPF on 2021/4/2.
 */
public class ContainerNavigator implements INavigator<Class<? extends IViewProcessor>, Intent, Intent> {

    private IViewContainer mContainer;

    public ContainerNavigator(IViewContainer mContainer) {
        this.mContainer = mContainer;
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
    public void push(@NonNull Class<? extends IViewProcessor> target, @Nullable Intent params, @NonNull IDataCallback<Intent> callback) {
        final IViewContainer viewContainer = mContainer;
        if (viewContainer == null || !viewContainer.getState().isLiving()) {
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

        if (viewContainer instanceof Fragment) {

        } else if (viewContainer instanceof androidx.fragment.app.Fragment) {

        }
        viewContainer.startActivityForResult(intent, AppConst.DEF_REQUEST_CODE);
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
        final IViewContainer viewContainer = mContainer;
        if (viewContainer == null || viewContainer.living()) {
            return;
        }
        viewContainer.finishWithResult(resultCode, data);
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

    public void setContainer(IViewContainer container) {
        this.mContainer = container;
    }

    public IViewContainer getContainer() {
        return mContainer;
    }

    public void postData(final Intent data, final String targetName) {
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
}