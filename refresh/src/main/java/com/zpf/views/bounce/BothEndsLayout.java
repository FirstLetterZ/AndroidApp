package com.zpf.views.bounce;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Created by ZPF on 2022/3/15.
 */
class BothEndsLayout extends LinearLayout {
    protected boolean isFootLayout;
    private IBothEndsViewHandler headFootView;

    public BothEndsLayout(Context context) {
        this(context, false);
    }

    public BothEndsLayout(Context context, boolean isFootLayout) {
        super(context);
        this.isFootLayout = isFootLayout;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setOrientation(VERTICAL);
        if (isFootLayout) {
            setGravity(Gravity.TOP);
        } else {
            setGravity(Gravity.BOTTOM);
        }
    }

    public void setContentView(IBothEndsViewHandler bothEndsView) {
        this.headFootView = bothEndsView;
        removeAllViews();
        if (bothEndsView != null) {
            View view = bothEndsView.onCreateView(this, isFootLayout);
            if (view != null) {
                addView(view);
            }
        }
    }

    public boolean isFootLayout() {
        return isFootLayout;
    }

    public void changeState(int sate) {
        if (headFootView != null) {
            headFootView.onStateChange(sate);
        }
    }

    public void setType(int type) {
        if (headFootView != null) {
            headFootView.onTypeChange(type);
        }
    }

    public int getDistHeight() {
        if (headFootView != null) {
            return headFootView.getDistHeight();
        }
        return 0;
    }
}