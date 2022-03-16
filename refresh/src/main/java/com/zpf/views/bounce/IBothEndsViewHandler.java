package com.zpf.views.bounce;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * @author Created by ZPF on 2022/3/15.
 */
public interface IBothEndsViewHandler {
    void onStateChange(int sate);

    void onTypeChange(int type);

    int getDistHeight();

    View onCreateView(@NonNull ViewGroup parent, boolean isFootLayout);
}
