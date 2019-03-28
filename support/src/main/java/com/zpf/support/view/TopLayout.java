package com.zpf.support.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zpf.frame.ITitleBar;
import com.zpf.frame.ITopLayout;
import com.zpf.tool.ViewUtil;

/**
 * Created by ZPF on 2019/3/28.
 */

public class TopLayout extends LinearLayout implements ITopLayout {
    private View statusBar;
    private ITitleBar titleBar;

    public TopLayout(Context context) {
        this(context, null, 0);
    }

    public TopLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        statusBar = new View(context);
        statusBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewUtil.getStatusBarHeight(context)));
        addView(statusBar);
        titleBar = new TitleBar(context);
        addView((TitleBar) titleBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ((TitleBar) titleBar).defHeight));
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
    public ViewGroup getLayout() {
        return this;
    }
}
