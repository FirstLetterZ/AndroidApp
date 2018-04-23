package com.zpf.appLib.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zpf.appLib.view.interfaces.TitleBarInterface;

/**
 * Created by ZPF on 2018/4/16.
 */

public abstract class TitleBarLayout extends RelativeLayout implements TitleBarInterface {
    public TitleBarLayout(Context context) {
        super(context);
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
