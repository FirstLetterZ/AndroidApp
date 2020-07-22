package com.zpf.support.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.zpf.support.R;
import com.zpf.tool.PublicUtil;

public class ShadowTopLayout extends TopLayout {
    private BottomShadow bottomShadow;

    public ShadowTopLayout(Context context) {
        this(context, null);
    }

    public ShadowTopLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowTopLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bottomShadow = new BottomShadow(context);
        bottomShadow.setElevation(2);
        bottomShadow.setShadowColor(PublicUtil.getColor(R.color.bottom_shadow));
        addView(bottomShadow, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public BottomShadow getBottomShadow() {
        return bottomShadow;
    }
}
