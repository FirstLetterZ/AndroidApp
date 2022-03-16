package com.zpf.views.bounce;

import android.view.View;

public interface IViewPullChecker {
    boolean checkPullUp(View view);

    boolean checkPullDown(View view);
}