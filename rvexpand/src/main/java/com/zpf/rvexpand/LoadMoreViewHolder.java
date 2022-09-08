package com.zpf.rvexpand;

import android.view.View;

public interface LoadMoreViewHolder {
    int HOLDER_TYPE = Integer.MAX_VALUE - 100;
    int HOLDER_ID = Integer.MAX_VALUE - 100;

    void onInit();

    void onLoading();

    void onComplete(boolean hasMore);

    View getItemView();
}