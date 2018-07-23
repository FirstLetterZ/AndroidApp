package com.zpf.baselib.ui.refresh;

import android.view.View;

/**
 * Created by ZPF on 2017/11/15.
 */

public interface ViewStateCheckListener {
    boolean checkPullUp(View view);
    boolean checkPullDown(View view);
    void relayout(View view, float pullDownY, float pullUpY);
}
