package com.zpf.refresh.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zpf.refresh.util.HeadFootInterface;

/**
 * Created by ZPF on 2018/11/27.
 */
public class HeadFootLayout extends LinearLayout {
    protected boolean isFootLayout;
    private HeadFootInterface headFootInterface;

    public HeadFootLayout(Context context) {
        this(context, false);
    }

    public HeadFootLayout(Context context, boolean isFootLayout) {
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

    public void setHeadFootInterface(HeadFootInterface headFootInterface) {
        this.headFootInterface = headFootInterface;
        removeAllViews();
        if (headFootInterface != null) {
            View view = headFootInterface.onCreateView(this, isFootLayout);
            if (view != null) {
                view.setBackgroundColor(Color.GREEN);
                addView(view);
            }
        }
    }

    public boolean isFootLayout() {
        return isFootLayout;
    }

    public void changeState(int sate) {
        if (headFootInterface != null) {
            headFootInterface.onStateChange(sate);
        }
    }

    public void setType(int type) {
        if (headFootInterface != null) {
            headFootInterface.onTypeChange(type);
        }
    }

    public int getDistHeight() {
        if (headFootInterface != null) {
            return headFootInterface.getDistHeight();
        }
        return 0;
    }
}
