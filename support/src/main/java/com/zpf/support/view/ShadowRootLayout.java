package com.zpf.support.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.ViewGroup;

import com.zpf.frame.IShadowLine;
import com.zpf.support.R;
import com.zpf.tool.PublicUtil;

public class ShadowRootLayout extends ContainerRootLayout {
    private LinearShadowLine linearShadowLine;

    public ShadowRootLayout(Context context) {
        super(context);
    }

    public ShadowRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initConfig(Context context, AttributeSet attrs) {
        super.initConfig(context, attrs);
        linearShadowLine = new LinearShadowLine(context);
        linearShadowLine.setElevation(2);
        linearShadowLine.setShadowColor(PublicUtil.getColor(R.color.bottom_shadow));
        addContentDecoration(linearShadowLine, -1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public IShadowLine getShadowLine() {
        return linearShadowLine;
    }
}
