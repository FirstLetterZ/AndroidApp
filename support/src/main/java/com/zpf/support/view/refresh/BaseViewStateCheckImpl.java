package com.zpf.support.view.refresh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

import com.zpf.tool.ViewUtil;
import com.zpf.support.api.PackedLayoutInterface;
import com.zpf.support.view.StickyNavLayout;

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
        } else if (view instanceof StickyNavLayout) {
            if ((pullDownY + pullUpY) >= 0) {
                view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                        (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
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
                view.layout(0, newTop, view.getMeasuredWidth(),
                        newTop + view.getMeasuredHeight());
                int headerHeight = ((StickyNavLayout) view).getHeaderViewHeight();
                int navViewHeight = ((StickyNavLayout) view).getNavViewHeight();
                View contentView = ((StickyNavLayout) view).getChildAt(2);
                contentView.layout(0, headerHeight + navViewHeight, contentView.getMeasuredWidth(),
                        (int) (view.getMeasuredHeight() + headerHeight + navViewHeight + dY));
                contentView.scrollTo(0, (int) (-dY));//滚到到最下面一条
            }
        } else {
            view.layout(0, (int) (pullDownY + pullUpY), view.getMeasuredWidth(),
                    (int) (pullDownY + pullUpY) + view.getMeasuredHeight());
        }
    }
}
