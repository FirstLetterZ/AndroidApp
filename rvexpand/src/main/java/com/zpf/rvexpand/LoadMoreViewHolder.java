package com.zpf.rvexpand;

import android.view.View;

public interface LoadMoreViewHolder {
    int HOLDER_TYPE = -99;

    void onLoading();

    void onComplete(boolean hasMore);

    View getItemView();
}
