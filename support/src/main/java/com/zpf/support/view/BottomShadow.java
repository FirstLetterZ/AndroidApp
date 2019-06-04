package com.zpf.support.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.frame.IShadowLine;

/**
 * Created by ZPF on 2019/3/28.
 */

public class BottomShadow extends View implements IShadowLine {
    private int elevation;

    public BottomShadow(Context context) {
        super(context);
    }

    public BottomShadow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomShadow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setElevation(int elevation) {
        if (elevation > 0 && this.elevation != elevation) {
            this.elevation = elevation;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = (int) (elevation * getResources().getDisplayMetrics().density);
            } else {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (elevation * getResources().getDisplayMetrics().density));
            }
            setLayoutParams(layoutParams);
        }
    }

    @Override
    public void setShadowColor(@ColorInt int startColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, Color.TRANSPARENT});
        setBackground(gradientDrawable);
    }

    @Override
    public void setShadowDrwable(Drawable background) {
        setBackground(background);
    }

    @Override
    public View getView() {
        return this;
    }

}
