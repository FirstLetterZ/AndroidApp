package com.zpf.support.view.banner;

import android.view.View;

/**
 * Created by ZPF on 2018/9/27.
 */

public interface BannerViewCreator {
    View createView(int position);

    int getSize();

    void onItemClick(int position);
}
