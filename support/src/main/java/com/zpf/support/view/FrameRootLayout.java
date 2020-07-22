package com.zpf.support.view;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpf.frame.IRootLayout;
import com.zpf.frame.IShadowLine;
import com.zpf.frame.ITitleBar;
import com.zpf.frame.ITopLayout;
import com.zpf.tool.config.GlobalConfigImpl;

public class FrameRootLayout extends FrameLayout implements IRootLayout {
    private ITopLayout topView;
    private View statusBar;
    private ITitleBar titleBar;
    private BottomShadow bottomShadow;
    private View contentView;

    public FrameRootLayout(Context context) {
        this(context, null, 0);
    }

    public FrameRootLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameRootLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFitsSystemWindows(true);
        createChildren(context);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }

    protected void createChildren(Context context) {
        topView = new ShadowTopLayout(context);
        statusBar = topView.getStatusBar();
        titleBar = topView.getTitleBar();
        bottomShadow = ((ShadowTopLayout) topView).getBottomShadow();
        addView(topView.getLayout(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        GlobalConfigImpl.get().onObjectInit(this);
    }

    @Override
    public View getStatusBar() {
        return statusBar;
    }

    @Override
    public ITitleBar getTitleBar() {
        return titleBar;
    }

    @Override
    public IShadowLine getShadowLine() {
        return bottomShadow;
    }

    @Override
    public ITopLayout getTopLayout() {
        return topView;
    }

    @Override
    public void changeTitleBar(@NonNull ITitleBar titleBar) {
        this.topView.getLayout().removeAllViews();
        if (statusBar != null) {
            this.topView.getLayout().addView(statusBar);
        }
        if (titleBar.getLayout() != null) {
            this.topView.getLayout().addView(titleBar.getLayout());
        }
        this.titleBar = titleBar;
    }

    @Override
    public void setContentView(@NonNull View view) {
        if (view == contentView) {
            return;
        }
        if (contentView != null) {
            removeView(contentView);
        }
        contentView = view;
        addView(view, 0);
        bringChildToFront(topView.getLayout());
    }

    @Override
    public void setContentView(@Nullable LayoutInflater inflater, int layoutId) {
        if (contentView != null) {
            removeView(contentView);
        }
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }
        contentView = inflater.inflate(layoutId, this, false);
        addView(contentView, 0);
        bringChildToFront(topView.getLayout());
    }

    @Override
    public ViewGroup getLayout() {
        return this;
    }

    @Override
    public FrameLayout getContentLayout() {
        return this;
    }

    @Override
    public View getContentView() {
        return contentView;
    }
}

