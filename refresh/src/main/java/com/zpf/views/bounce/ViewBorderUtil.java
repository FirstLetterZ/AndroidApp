package com.zpf.views.bounce;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

public class ViewBorderUtil {
    public static boolean isViewToTop(View view) {
        if (view == null) {
            return false;
        }
        return view.getScrollY() == 0;
    }

    public static boolean isAbsListViewToTop(AbsListView absListView) {
        if (absListView == null) {
            return false;
        } else {
            int firstChildTop = 0;
            if (absListView.getChildCount() > 0) {
                View childFirst = absListView.getChildAt(0);
                firstChildTop = childFirst.getTop() - absListView.getPaddingTop();
            }
            return absListView.getFirstVisiblePosition() == 0 && firstChildTop == 0;
        }
    }

    public static boolean isAbsListViewToBottom(AbsListView absListView) {
        if (absListView != null && absListView.getAdapter() != null && absListView.getChildCount() > 0
                && absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1) {
            View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
            return lastChild.getBottom() <= absListView.getMeasuredHeight();
        } else {
            return false;
        }
    }

    public static boolean isViewToBottom(View view) {
        if (view == null) {
            return false;
        }
        return view.getHeight() + view.getScrollY() >= view.getMeasuredHeight();
    }

    public static boolean isViewGroupToBottom(ViewGroup view) {
        if (view == null) {
            return false;
        }
        int childCount = view.getChildCount();
        if (childCount == 0) {
            return true;
        }
        int maxBottom = 0;
        View child;
        ViewGroup.LayoutParams lp;
        int marginBottom = 0;
        while (childCount > 0) {
            child = view.getChildAt(childCount - 1);
            if (child != null && child.getVisibility() != View.GONE) {
                lp = child.getLayoutParams();
                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    marginBottom = ((ViewGroup.MarginLayoutParams) lp).bottomMargin;
                }
                maxBottom = Math.max(maxBottom, child.getBottom() + marginBottom);
            }
            childCount--;
        }
        return view.getHeight() + view.getScrollY() >= maxBottom + view.getPaddingBottom();
    }

    public static boolean isWebViewToBottom(WebView view) {
        return view != null && (float) view.getScrollY() >= (float) view.getContentHeight() * view.getScale() - (float) view.getMeasuredHeight();
    }

}
