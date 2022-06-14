package com.zpf.support.view;

import android.content.Context;
import android.util.AttributeSet;

import com.zpf.views.LinearShadowLine;
import com.zpf.views.type.IShadowLine;

public class ShadowRootLayout extends PhonePageLayout {
    private LinearShadowLine linearShadowLine;

    public ShadowRootLayout(Context context) {
        super(context);
    }

    public ShadowRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linearShadowLine = new LinearShadowLine(context);
        addDecoration(linearShadowLine, 1);
    }

    public IShadowLine getShadowLine() {
        return linearShadowLine;
    }
}
