package com.zpf.baselib.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class LinearLayoutManagerWrap extends LinearLayoutManager {
    public LinearLayoutManagerWrap(Context context) {
        super(context);
    }

    public LinearLayoutManagerWrap(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerWrap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public void setAutoMeasureEnabled(boolean enabled) {
        super.setAutoMeasureEnabled(true);
    }
}
