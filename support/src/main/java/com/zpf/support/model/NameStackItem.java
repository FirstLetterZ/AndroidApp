package com.zpf.support.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zpf.frame.IViewContainer;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.constant.AppConst;
import com.zpf.tool.config.stack.IStackItem;
import com.zpf.tool.config.stack.StackElementState;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class NameStackItem implements IStackItem {
    private String name;
    private int elementState = StackElementState.STACK_OUTSIDE;
    private WeakReference<Activity> mInstance;

    public NameStackItem(String name) {
        this.name = name;
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
