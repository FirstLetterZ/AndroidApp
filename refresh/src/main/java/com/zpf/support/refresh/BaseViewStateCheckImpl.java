package com.zpf.support.refresh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

/**
 * Created by ZPF on 2017/11/15.
 */
public class BaseViewStateCheckImpl implements ViewStateCheckListener {

    @Override
    public boolean checkPullUp(View view) {
        if (view == null) {
            return false;
        } else if (view instanceof WebView) {
            return ViewUtil.isWebViewToBottom((WebView) view);
        } else if (view instanceof AbsListView) {
            return ViewUtil.isAbsListViewToBottom((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return ViewUtil.isRecyclerViewToBottom((RecyclerView) view);
        } else if (view instanceof PackedLayoutInterface) {
            return checkPullUp(((PackedLayoutInterface) view).getCurrentChild());
        } else {
            return !(view instanceof ViewGroup) || ViewUtil.isViewGroupToBottom((ViewGroup) view);
        }
    }

    @Override
    public boolean checkPullDown(View view) {
        if (view == null) {
            return false;
        } else if (view instanceof AbsListView) {
            return ViewUtil.isAbsListViewToTop((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return ViewUtil.isRecyclerViewToTop((RecyclerView) view);
        } else if (view instanceof PackedLayoutInterface) {
            return checkPullDown(((PackedLayoutInterface) view).getCurrentChild());
        } else {
            return ViewUtil.isViewToTop(view);
        }
    }

    @Override
    public void relayout(View view, float pullDownY, float pullUpY) {
        if (view == null) {
            return;
        } else {
            view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                    (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
        }
    }
}
