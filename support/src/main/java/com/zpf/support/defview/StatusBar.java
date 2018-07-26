package com.zpf.support.defview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZPF on 2018/6/18.
 */
public class StatusBar extends View {
    public StatusBar(Context context) {
        this(context,null,0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setBackgroundColor(int color) {
        if (color == Color.WHITE) {
                color = Color.parseColor("#40999999");
        }
        super.setBackgroundColor(color);
    }
}
