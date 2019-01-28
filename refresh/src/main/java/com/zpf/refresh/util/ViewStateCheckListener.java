package com.zpf.refresh.util;

import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by ZPF on 2017/11/15.
 */

public interface ViewStateCheckListener {
    boolean checkPullUp(View view);

    boolean checkPullDown(View view);

    void relayout(View view, RelativeLayout.LayoutParams params, float pullDownY, float pullUpY);
}
