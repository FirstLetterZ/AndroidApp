package com.zpf.support.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.zpf.support.api.PackedLayoutInterface;

/**
 * Created by ZPF on 2018/7/7.
 */
public abstract class PackedLayout<T extends View> extends FrameLayout implements PackedLayoutInterface {
    protected View loadView;
    protected View errorView;
    protected View emptyView;
    protected T contentView;

    public PackedLayout(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
        addChild(contentView);
        addChild(emptyView);
        addChild(errorView);
        addChild(loadView);
        showContent();
    }

    public PackedLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
        addChild(contentView);
        addChild(emptyView);
        addChild(errorView);
        addChild(loadView);
        showContent();
    }

    public PackedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        addChild(errorView);
        addChild(emptyView);
        addChild(contentView);
        addChild(loadView);
        showContent();
    }

    //初始化全部要用的视图
    public abstract void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr);

    @NonNull
    @Override
    public View getCurrentChild() {
        if (loadView != null && loadView.getVisibility() == View.VISIBLE) {
            return loadView;
        } else if (emptyView != null && emptyView.getVisibility() == View.VISIBLE) {
            return emptyView;
        } else if (contentView != null && contentView.getVisibility() == View.VISIBLE) {
            return contentView;
        } else if (errorView != null) {
            errorView.setVisibility(VISIBLE);
            return errorView;
        } else {
            throw new NullPointerException("no available view!");
        }
    }

    private void addChild(View view) {
        if (view != null) {
            addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }
    }

    public void showLoading() {
        if (loadView != null) {
            loadView.setVisibility(VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
    }

    public void showContent() {
        if (loadView != null) {
            loadView.setVisibility(GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(VISIBLE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
    }

    public void showEmpty() {
        if (loadView != null) {
            loadView.setVisibility(GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(VISIBLE);
        }
        if (contentView != null) {
            contentView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
    }

    public void showError() {
        if (loadView != null) {
            loadView.setVisibility(GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(VISIBLE);
        }
    }

    public T getContentView() {
        return contentView;
    }
}
