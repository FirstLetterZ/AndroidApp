package com.zpf.support.model;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.tool.stack.IStackItem;
import com.zpf.tool.stack.StackElementState;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class NameStackItem implements IStackItem {
    private final String name;
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
