package com.zpf.support.view.banner;

import android.content.Context;
import android.view.View;

/**
 * Created by ZPF on 2018/9/27.
 */

public interface BannerViewCreator {
    View onCreateView(Context context, int position);

    int getSize();

    void onBindView(View view, int position);
}
