package com.zpf.support.defview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zpf.support.interfaces.RootLayoutInterface;
import com.zpf.support.interfaces.TitleBarInterface;
import com.zpf.support.generalUtil.ViewUtil;


/**
 * Created by ZPF on 2018/6/14.
 */
public class RootLayout extends LinearLayout implements RootLayoutInterface {
    private StatusBar statusBar;
    private TitleBarInterface titleBarLayout;
    private FrameLayout contentLayout;
    private boolean hasAddChildren;

    public RootLayout(TitleBarInterface titleBarView) {
        this(titleBarView.getLayout().getContext(), null, 0);
        this.titleBarLayout = titleBarView;
    }

    public RootLayout(Context context) {
        this(context, null, 0);
    }

    public RootLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RootLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    private void createChildren(Context context) {
        statusBar = new StatusBar(context);
        int statusBarHeight = ViewUtil.getStatusBarHeight(context);
        statusBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        addView(statusBar);
        if (titleBarLayout == null || titleBarLayout.getLayout() == null) {
            titleBarLayout = new TitleBar(context);
        }
        addView(titleBarLayout.getLayout());
        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(contentLayout);
        hasAddChildren = true;
    }

    @Override
    public View getStatusBar() {
        return statusBar;
    }

    @Override
    public void setTitleBar(@NonNull TitleBarInterface titleBar) {
        this.titleBarLayout = titleBar;
        if (hasAddChildren) {
            removeViewAt(1);
            addView(titleBarLayout.getLayout(), 1);
        }
    }

    @Override
    public TitleBarInterface getTitleBar() {
        return titleBarLayout;
    }

    @Override
    public void setTopViewBackground(Drawable drawable) {
        titleBarLayout.getLayout().setBackground(drawable);
        statusBar.setBackground(drawable);
    }

    @Override
    public void setTopViewBackground(@ColorInt int color) {
        titleBarLayout.getLayout().setBackgroundColor(color);
        statusBar.setBackgroundColor(color);
    }


    @Override
    public void setContentView(@NonNull View view) {
        contentLayout.removeAllViews();
        contentLayout.addView(view);
    }

    @Override
    public void setContentView(@NonNull LayoutInflater inflater, int layoutId) {
        contentLayout.removeAllViews();
        inflater.inflate(layoutId, contentLayout, true);
    }

    @Override
    public ViewGroup getLayout() {
        return this;
    }

    @Override
    public FrameLayout getContentLayout() {
        return contentLayout;
    }
}
