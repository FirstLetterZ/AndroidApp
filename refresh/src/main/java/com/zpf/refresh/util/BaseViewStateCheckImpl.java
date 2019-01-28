package com.zpf.refresh.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

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
    public void relayout(View view, RelativeLayout.LayoutParams params, float pullDownY, float pullUpY) {
        if (view == null) {
            return;
        }
        float dY = pullDownY + pullUpY;
        if (view instanceof StickyNavLayout) {
            View contentView = ((StickyNavLayout) view).getChildAt(2);
            int contentHeight = ((StickyNavLayout) view).getContentViewHeight();
            int newContentBottom;
            if (dY >= 0) {
                newContentBottom = contentView.getTop() + contentHeight;
            } else {
                int remainHeaderHeight = ((StickyNavLayout) view).getHeaderViewHeight() - view.getScrollY();
                if (remainHeaderHeight > 0 && remainHeaderHeight + dY < 0) {
                    dY = -remainHeaderHeight;
                } else {
                    dY = 0;
                }
                newContentBottom = (int) (contentView.getTop() + contentHeight + dY);
            }
            view.layout(params.leftMargin, (int) dY + params.topMargin,
                    params.leftMargin + view.getMeasuredWidth(),
                    (int) dY + params.topMargin + view.getMeasuredHeight() + params.topMargin + params.bottomMargin);
            contentView.layout(contentView.getLeft(), contentView.getTop(), contentView.getRight(), newContentBottom);
            if (dY > 0) {
                if (contentView.getScrollY() != 0) {
                    contentView.scrollTo(0, 0);
                }
            } else {
                int scrollY = contentView.getBottom() - newContentBottom;
                if (scrollY != 0) {
                    if (dY == 0) {
                        if (contentView.getScrollY() != 0) {
                            contentView.scrollBy(0, scrollY);//抵消剩余的滚动
                        }
                    } else {
                        contentView.scrollBy(0, scrollY);//滚到到最下面一条
                    }
                }
            }
        } else {
            view.layout(params.leftMargin, (int) dY + params.topMargin,
                    params.leftMargin + view.getMeasuredWidth(),
                    params.topMargin + (int) dY + params.bottomMargin + view.getMeasuredHeight());
        }
    }
}
