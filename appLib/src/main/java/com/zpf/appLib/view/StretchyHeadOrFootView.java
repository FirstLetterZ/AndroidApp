package com.zpf.appLib.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by ZPF on 2017/10/26.
 */
public abstract class StretchyHeadOrFootView {
    private RelativeLayout containerLayout;
    protected boolean isLoadLayout = false;
    protected float density;
    protected int state = -1;
    protected int type = -1;

    public StretchyHeadOrFootView(Context context) {
        this(context, false);
    }

    public StretchyHeadOrFootView(Context context, boolean isLoadLayout) {
        this.isLoadLayout = isLoadLayout;
        density = context.getResources().getDisplayMetrics().density;

        containerLayout = new RelativeLayout(context);
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        View content = getRefreshLayout();
        if (content != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) content.getLayoutParams();
            if (params == null) {
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            }
            if (isLoadLayout) {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            }
            containerLayout.addView(content, params);
        }
    }

    protected Context getContext() {
        return containerLayout.getContext();
    }

    public abstract void changeState(int sate);

    public abstract void setType(int type);

    public abstract int getDistHeight();

    public abstract View getRefreshLayout();

    public RelativeLayout getContainerLayout() {
        return containerLayout;
    }

}
