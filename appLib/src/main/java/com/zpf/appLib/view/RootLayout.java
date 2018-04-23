package com.zpf.appLib.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zpf.appLib.view.def.DefaultTitleBar;
import com.zpf.appLib.util.ViewUtil;

/**
 * Created by ZPF on 2018/4/13.
 */
public class RootLayout extends LinearLayout {
    private View statusBar;
    private TitleBarLayout titleBarLayout;
    private FrameLayout contentLayout;
    private boolean hasAddChildren;

    public RootLayout(Context context) {
        this(context, null, 0);
    }

    public RootLayout(TitleBarLayout titleBarView) {
        this(titleBarView.getContext(), null, 0);
        this.titleBarLayout = titleBarView;
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
        statusBar = new View(context);
        int statusBarHeight = ViewUtil.getStatusBarHeight(context);
        statusBar.setBackgroundColor(Color.BLUE);
        statusBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        addView(statusBar);
        if (titleBarLayout == null) {
            titleBarLayout = new DefaultTitleBar(context);
        }
        addView(titleBarLayout);
        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addView(contentLayout);
        hasAddChildren = true;
    }

    public void showStatusBarColorId(int colorId){
        statusBar.setBackgroundColor(getContext().getResources().getColor(colorId));
        statusBar.setVisibility(VISIBLE);
    }

    public void showStatusBarColorInt(@ColorInt int colorInt){
        statusBar.setBackgroundColor(colorInt);
        statusBar.setVisibility(VISIBLE);
    }

    public View getStatusBar() {
        return statusBar;
    }

    public void setTitleBarLayout(TitleBarLayout titleBarLayout) {
        this.titleBarLayout = titleBarLayout;
        if (hasAddChildren) {
            removeViewAt(1);
            addView(titleBarLayout, 1);
        }
    }

    public TitleBarLayout getTitleBarLayout() {
        return titleBarLayout;
    }

    public void setContentView(View view) {
        contentLayout.removeAllViews();
        contentLayout.addView(view);
    }

    public void setContentView(LayoutInflater inflater, int layoutId) {
        contentLayout.removeAllViews();
        inflater.inflate(layoutId, contentLayout, true);
    }

}
