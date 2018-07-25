package com.zpf.support.webview;

import android.view.View;

public interface OnProgressChangedListener<T extends View> {
    /**
     * @param view 对应视图
     * @param total 总数据量
     * @param current 当前进度量
     */
    void onProgressChanged(T view, long total, long current);
}
