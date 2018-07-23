package com.zpf.baselib.ui.refresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by ZPF on 2018/6/15.
 */
public abstract class AbsHeadFootLayout extends RelativeLayout {
    protected boolean isLoadLayout = false;
    protected int state = -1;
    protected int type = -1;
    public AbsHeadFootLayout(Context context) {
        this(context, false);
    }

    public AbsHeadFootLayout(Context context, boolean isLoadLayout) {
        super(context);
        this.isLoadLayout = isLoadLayout;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        View content = getRefreshLayout();
        if (content != null) {
            LayoutParams params = (LayoutParams) content.getLayoutParams();
            if (params == null) {
                params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            }
            if (isLoadLayout) {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            }
            addView(content, params);
        }
    }

    public boolean isLoadLayout() {
        return isLoadLayout;
    }

    public abstract void changeState(int sate);

    public abstract void setType(int type);

    public abstract int getDistHeight();

    public abstract View getRefreshLayout();

    public RelativeLayout getContainerLayout() {
        return this;
    }

}
