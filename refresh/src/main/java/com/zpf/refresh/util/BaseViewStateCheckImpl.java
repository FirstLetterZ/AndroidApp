package com.zpf.refresh.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

import com.zpf.api.PackedLayoutInterface;
import com.zpf.refresh.view.StickyNavLayout;

/**
 * Created by ZPF on 2017/11/15.
 */
public class BaseViewStateCheckImpl implements ViewStateCheckListener {

    @Override
    public boolean checkPullUp(View view) {
        if (view == null) {
            return false;
        } else if (view instanceof WebView) {
            return ViewBorderUtil.isWebViewToBottom((WebView) view);
        } else if (view instanceof AbsListView) {
            return ViewBorderUtil.isAbsListViewToBottom((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return ViewBorderUtil.isRecyclerViewToBottom((RecyclerView) view);
        } else if (view instanceof PackedLayoutInterface) {
            return checkPullUp(((PackedLayoutInterface) view).getCurrentChild());
        } else if (view instanceof StickyNavLayout) {
            return ((StickyNavLayout) view).isContentViewToBottom();
        } else {
            return !(view instanceof ViewGroup) || ViewBorderUtil.isViewGroupToBottom((ViewGroup) view);
        }
    }

    @Override
    public boolean checkPullDown(View view) {
        if (view == null) {
            return false;
        } else if (view instanceof AbsListView) {
            return ViewBorderUtil.isAbsListViewToTop((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return ViewBorderUtil.isRecyclerViewToTop((RecyclerView) view);
        } else if (view instanceof PackedLayoutInterface) {
            return checkPullDown(((PackedLayoutInterface) view).getCurrentChild());
        } else {
            return ViewBorderUtil.isViewToTop(view);
        }
    }

    @Override
    public void relayout(View view, float pullDownY, float pullUpY) {
        if (view == null) {
            return;
        } else if (view instanceof StickyNavLayout) {
            View contentView = ((StickyNavLayout) view).getChildAt(2);
            int height = ((StickyNavLayout) view).getAllChildHeight();
            if ((pullDownY + pullUpY) >= 0) {
                view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                        (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
                contentView.layout(0, contentView.getTop(), contentView.getMeasuredWidth(), height);
                if (contentView.getScrollY() != 0) {
                    contentView.scrollTo(0, 0);
                }
            } else {
                float dY = pullDownY + pullUpY;
                int newTop = view.getTop();
                int remainHeaderHeight = ((StickyNavLayout) view).getHeaderViewHeight()
                        - view.getScrollY();
                if (remainHeaderHeight > 0) {
                    if (remainHeaderHeight + dY < 0) {
                        dY = remainHeaderHeight + dY;
                        newTop = -remainHeaderHeight;
                    } else {
                        newTop = (int) dY;
                        dY = 0;
                    }
                }
                view.layout(0, newTop, view.getMeasuredWidth(), newTop + view.getMeasuredHeight());
                contentView.layout(0, contentView.getTop(), contentView.getMeasuredWidth(), (int) (height + dY));
                contentView.scrollTo(0, (int) (-dY));//滚到到最下面一条
            }
        } else {
            view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                    (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
        }
    }
}
