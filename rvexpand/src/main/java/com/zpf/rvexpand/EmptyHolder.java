package com.zpf.rvexpand;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.api.IHolder;

public class EmptyHolder extends RecyclerView.ViewHolder implements IHolder<View> {
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;

    public EmptyHolder(@NonNull Context context) {
        super(new Space(context));
    }

    public EmptyHolder(@NonNull Context context, int height, int width) {
        super(new Space(context));
        itemView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        this.height = height;
        this.width = width;
    }

    public EmptyHolder(@NonNull Context context, @NonNull ViewGroup.LayoutParams layoutParams) {
        super(new Space(context));
        itemView.setLayoutParams(layoutParams);
        this.height = layoutParams.height;
        this.width = layoutParams.width;
    }

    public EmptyHolder(@NonNull View emptyView, @NonNull ViewGroup.LayoutParams layoutParams) {
        super(emptyView);
        itemView.setLayoutParams(layoutParams);
        this.height = layoutParams.height;
        this.width = layoutParams.width;
    }

    public void checkSizeChanged() {
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if (layoutParams == null || (layoutParams.height == height && layoutParams.width == width)) {
            return;
        }
        height = layoutParams.height;
        width = layoutParams.width;
        boolean shouldRequestLayout = (height > 0 && itemView.getMeasuredWidth() != height)
                || (width > 0 && itemView.getMeasuredWidth() != width)
                || (height == RecyclerView.LayoutParams.WRAP_CONTENT && itemView.getMeasuredHeight() > 0)
                || (width == RecyclerView.LayoutParams.WRAP_CONTENT && itemView.getMeasuredWidth() > 0)
                || (height == RecyclerView.LayoutParams.MATCH_PARENT && itemView.getMeasuredHeight() == 0)
                || (width == RecyclerView.LayoutParams.MATCH_PARENT && itemView.getMeasuredWidth() == 0);
        if (shouldRequestLayout) {
            itemView.requestLayout();
        }
    }

    @Override
    public void onBindData(@Nullable Object data, int position) {
    }

    @Override
    public void onReceiveListener(@Nullable Object listener, int type) {
    }

    @Override
    public View getRoot() {
        return itemView;
    }

    @Override
    public View findById(int id) {
        return itemView.findViewById(id);
    }

    @Override
    public View findByTag(String tag) {
        return itemView.findViewWithTag(tag);
    }
}
