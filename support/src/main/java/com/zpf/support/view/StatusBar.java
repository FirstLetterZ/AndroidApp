package com.zpf.support.view;

import android.content.Context;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.tool.ViewUtil;

public class StatusBar extends View {

    private int statusBarHeight = ViewUtil.getStatusBarHeight(getContext());

    public StatusBar(Context context) {
        this(context, null, 0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = statusBarHeight;
        }
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        if (h != statusBarHeight) {
            getLayoutParams().height = statusBarHeight;
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    statusBarHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
