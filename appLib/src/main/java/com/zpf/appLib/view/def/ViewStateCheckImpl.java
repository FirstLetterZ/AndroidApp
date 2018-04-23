package com.zpf.appLib.view.def;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

import com.zpf.appLib.util.ViewUtil;
import com.zpf.appLib.view.StickyNavLayout;
import com.zpf.appLib.view.interfaces.ViewStateCheckListener;

/**
 * Created by ZPF on 2017/11/15.
 */
public class ViewStateCheckImpl implements ViewStateCheckListener {

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
        } else if (view instanceof StickyNavLayout) {
            return ((StickyNavLayout) view).isContentViewToBottom();
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
        } else {
            return ViewUtil.isViewToTop(view);
        }
    }

    @Override
    public void relayout(View view, float pullDownY, float pullUpY) {
        if (view == null) {
            return;
        } else if (view instanceof StickyNavLayout) {
            int remainHeaderHeight = ((StickyNavLayout) view).getHeaderViewHeight() - view.getScrollY();
            if (remainHeaderHeight + (pullDownY + pullUpY) < 0) {
                View childTwo = ((StickyNavLayout) view).getChildAt(2);
                view.layout(0, -remainHeaderHeight, view.getMeasuredWidth(), view.getMeasuredHeight() - (int) (pullDownY + pullUpY) - remainHeaderHeight);
                childTwo.layout(0, childTwo.getTop(), view.getMeasuredWidth(),
                        view.getMeasuredHeight() + (int) (pullDownY + pullUpY) + ((StickyNavLayout) view).getHeaderViewHeight());
                childTwo.scrollBy(0, (int) (-pullDownY - pullUpY));//滚到到最下面一条
            } else {
                view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                        (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
            }
        } else {
            view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                    (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
        }
    }

}
