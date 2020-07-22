package com.zpf.support.model;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.tool.config.stack.IStackItem;
import com.zpf.tool.config.stack.StackElementState;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class ContainerStackItem implements IStackItem {
    private String name;
    private int elementState = StackElementState.STACK_OUTSIDE;
    private WeakReference<Activity> mInstance;

    public ContainerStackItem(IViewContainer viewContainer) {
        this.mInstance = new WeakReference<>(viewContainer.getCurrentActivity());
        name = viewContainer.getParams().getString(AppConst.STACK_ITEM_NAME);
        if (TextUtils.isEmpty(name)) {
            IViewProcessor viewProcessor = viewContainer.getViewProcessor();
            if (viewProcessor == null) {
                try {
                    Class cls = (Class) viewContainer.getParams().getSerializable(AppConst.TARGET_VIEW_CLASS);
                    name = cls.getName();
                } catch (Exception e) {
                    name = viewContainer.getClass().getName();
                }
            } else {
                name = viewProcessor.getClass().getName();
            }
        }
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @StackElementState
    @Override
    public int getItemState() {
        return elementState;
    }

    @Override
    public void setItemState(@StackElementState int newState) {
        elementState = newState;
    }

    @Override
    public void bindActivity(Activity activity) {
        if (getStackActivity() == null) {
            mInstance = new WeakReference<>(activity);
        }
    }

    @Nullable
    @Override
    public Activity getStackActivity() {
        return mInstance.get();
    }

    @Nullable
    @Override
    public LinkedList<IStackItem> getInsideStack() {
        return null;
    }
}
