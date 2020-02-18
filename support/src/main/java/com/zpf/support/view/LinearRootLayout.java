package com.zpf.support.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zpf.frame.IRootLayout;
import com.zpf.frame.IShadowLine;
import com.zpf.frame.ITitleBar;
import com.zpf.frame.ITopLayout;
import com.zpf.support.R;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.config.GlobalConfigImpl;

/**
 * 带标题栏的默认布局
 * Created by ZPF on 2018/6/14.
 */
public class LinearRootLayout extends LinearLayout implements IRootLayout {
    private ITopLayout topView;
    private View statusBar;
    private ITitleBar titleBar;
    private BottomShadow bottomShadow;
    private FrameLayout contentLayout;
    private View contentView;

    public LinearRootLayout(Context context) {
        this(context, null, 0);
    }

    public LinearRootLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearRootLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setFitsSystemWindows(true);
        createChildren(context);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }

    protected void createChildren(Context context) {
        topView = new TopLayout(context);
        statusBar = topView.getStatusBar();
        titleBar = topView.getTitleBar();
        bottomShadow = new BottomShadow(context);
        bottomShadow.setElevation(2);
        bottomShadow.setShadowColor(PublicUtil.getColor(R.color.bottom_shadow));
        addView(topView.getLayout());
        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        contentLayout.addView(bottomShadow);
        addView(contentLayout);
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
            contentLayout.removeView(contentView);
        }
        contentView = view;
        contentLayout.addView(view, 0);
        bottomShadow.bringToFront();
    }

    @Override
    public void setContentView(@Nullable LayoutInflater inflater, int layoutId) {
        if (contentView != null) {
            contentLayout.removeView(contentView);
        }
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }
        contentView = inflater.inflate(layoutId, contentLayout, false);
        contentLayout.addView(contentView, 0);
        bottomShadow.bringToFront();
    }

    @Override
    public ViewGroup getLayout() {
        return this;
    }

    @Override
    public FrameLayout getContentLayout() {
        return contentLayout;
    }

    @Override
    public View getContentView() {
        return contentView;
    }
}
