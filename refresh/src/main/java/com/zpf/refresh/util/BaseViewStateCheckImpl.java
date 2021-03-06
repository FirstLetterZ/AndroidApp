package com.zpf.refresh.util;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.zpf.api.IPackedLayout;
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
        } else if (view instanceof IPackedLayout) {
            return checkPullUp(((IPackedLayout) view).getCurrentChild());
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
        } else if (view instanceof IPackedLayout) {
            return checkPullDown(((IPackedLayout) view).getCurrentChild());
        } else if (view instanceof StickyNavLayout) {
            return view.getScrollY() == 0 && ((StickyNavLayout) view).isContentViewToTop();
        } else {
            return ViewBorderUtil.isViewToTop(view);
        }
    }

    @Override
    public void relayout(View view, FrameLayout.LayoutParams params, float pullDownY, float pullUpY) {
        if (view == null) {
            return;
        }
        float dY = pullDownY + pullUpY;
        if (view instanceof StickyNavLayout) {
            View contentView = ((StickyNavLayout) view).getChildAt(2);
            int newViewTop;
            int newContentBottom;
            if (dY >= 0) {
                newViewTop = (int) dY + params.topMargin;
                newContentBottom = contentView.getTop() + contentView.getMeasuredHeight();
            } else {
                newViewTop = view.getTop();
                int remainHeaderHeight = ((StickyNavLayout) view).getHeaderViewHeight()
                        - view.getScrollY();
                if (remainHeaderHeight > 0) {
                    if (remainHeaderHeight + dY < 0) {
                        dY = remainHeaderHeight + dY;
                        newViewTop = params.topMargin - remainHeaderHeight;
                    } else {
                        newViewTop = (int) (params.topMargin + dY);
                        dY = 0;
                    }
                }
                newContentBottom = (int) (contentView.getTop() + contentView.getMeasuredHeight() + dY);
            }
            int scrollY = contentView.getBottom() - newContentBottom;
            view.layout(params.leftMargin, newViewTop, params.leftMargin + view.getMeasuredWidth(),
                    newViewTop + view.getMeasuredHeight());
            contentView.layout(contentView.getLeft(), contentView.getTop(), contentView.getRight(), newContentBottom);
            if (dY > 0 && contentView.getScrollY() != 0) {
                contentView.scrollTo(0, 0);//抵消剩余滚动
            } else if (dY == 0 && scrollY != 0 && contentView.getScrollY() != 0) {
                contentView.scrollBy(0, scrollY);//抵消剩余滚动
            } else if (dY < 0 && scrollY != 0) {
                contentView.scrollBy(0, scrollY);//滚到到最下面一条
            }
        } else {
            view.layout(params.leftMargin, (int) dY + params.topMargin,
                    params.leftMargin + view.getMeasuredWidth(),
                    params.topMargin + (int) dY + view.getMeasuredHeight());
        }
    }
}
