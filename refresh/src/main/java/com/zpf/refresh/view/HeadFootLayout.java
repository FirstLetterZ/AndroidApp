package com.zpf.refresh.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zpf.refresh.util.HeadFootInterface;

/**
 * Created by ZPF on 2018/11/27.
 */
public class HeadFootLayout extends RelativeLayout {
    protected boolean isFootLayout = false;
    private HeadFootInterface headFootInterface;
    private LayoutParams childParam;

    public HeadFootLayout(Context context) {
        this(context, false);
    }

    public HeadFootLayout(Context context, boolean isFootLayout) {
        super(context);
        this.isFootLayout = isFootLayout;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        childParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (isFootLayout) {
            childParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        } else {
            childParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        }
    }

    public void setHeadFootInterface(HeadFootInterface headFootInterface) {
        this.headFootInterface = headFootInterface;
        removeAllViews();
        if (headFootInterface != null) {
            View view = headFootInterface.onCreateView(this, isFootLayout);
            if (view != null) {
                childParam.height = headFootInterface.getDistHeight();
                addView(view, childParam);
            }
        }
    }

    public boolean isLoadLayout() {
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
