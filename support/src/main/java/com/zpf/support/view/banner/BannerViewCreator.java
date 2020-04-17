package com.zpf.support.view.banner;

import android.view.View;

/**
 * Created by ZPF on 2018/9/27.
 */

public interface BannerViewCreator {
    View onCreateView(int position);

    int getSize();

    void onBindView(View view, int position);
}
