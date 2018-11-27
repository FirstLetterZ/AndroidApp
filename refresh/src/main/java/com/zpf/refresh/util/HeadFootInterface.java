package com.zpf.refresh.util;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ZPF on 2018/11/27.
 */
public interface HeadFootInterface {

    void onStateChange(int sate);

    void onTypeChange(int type);

    int getDistHeight();

    View onCreateView(@NonNull ViewGroup parent, boolean isFootLayout);
}
