package com.zpf.support.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.tool.ViewUtil;

public class StatusBar extends View {
    public StatusBar(Context context) {
        this(context,null,0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewUtil.getStatusBarHeight(context)));
    }

}
