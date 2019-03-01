package com.zpf.tool.expand.view;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpf.api.IPackedLayout;

/**
 * Created by ZPF on 2018/7/7.
 */
public abstract class PackedLayout<T extends View> extends FrameLayout implements IPackedLayout {
    protected T contentView;
    private final SparseArray<View> childrenArray = new SparseArray<>();

    public PackedLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public PackedLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PackedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        contentView = createContentView(context, attrs, defStyleAttr);
        childrenArray.put(0, contentView);
        addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        initView(context, attrs, defStyleAttr);
    }

    protected abstract T createContentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr);

    //初始化全部要用的视图
    public abstract void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr);

    @NonNull
    @Override
    public View getCurrentChild() {
        View showingView = null;
        for (int i = 0; i < getChildCount(); i++) {
            if (showingView == null) {
                if (getChildAt(i).getVisibility() == VISIBLE) {
                    showingView = getChildAt(i);
                }
            } else {
                getChildAt(i).setVisibility(GONE);
            }
        }
        if (showingView == null) {
            throw new NullPointerException("no available view!");
        } else {
            return showingView;
        }
    }

    @Override
    public boolean showChildByKey(@IntRange(from = 1, to = 16) int key) {
        View showView = childrenArray.get(key);
        boolean result = false;
        if (showView != null) {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) == showView) {
                    getChildAt(i).setVisibility(VISIBLE);
                    result = true;
                } else {
                    getChildAt(i).setVisibility(GONE);
                }
            }
        }
        return result;
    }

    @Override
    public void addChildByKey(@IntRange(from = 1, to = 16) int key, View view) {
        if (view != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null || !(params instanceof MarginLayoutParams)) {
                params = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
            }
            childrenArray.put(key, view);
            addView(view, params);
        }
    }

    public void showContentView() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) != contentView) {
                getChildAt(i).setVisibility(GONE);
            }
        }
        contentView.setVisibility(VISIBLE);
    }

    public T getContentView() {
        return contentView;
    }
}
