package com.zpf.refresh.util;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.reflect.Field;

public class ViewBorderUtil {
    public static boolean isViewToTop(View view) {
        if (view == null) {
            return false;
        }
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            View child = null;
            int index = 0;
            while (index < childCount) {
                child = ((ViewGroup) view).getChildAt(index);
                if (child != null && child.getVisibility() != View.GONE) {
                    break;
                } else {
                    child = null;
                    index++;
                }
            }
            if (child != null) {
                Log.e("TAG", "child.getTop=" + child.getTop() + ";view.getTop=" + view.getTop() + ";canScrollVertically=" + view.canScrollVertically(1) + ";getScrollY=" + view.getScrollY());
                return child.getTop() >= view.getTop();
            }
            return true;
        }
        return view.getScrollY() == 0 && !view.canScrollVertically(1);
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

    public static boolean isRecyclerViewToTop(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        } else {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null || manager.getChildCount() == 0) {
                return true;
            } else {
                if (manager instanceof LinearLayoutManager) {
                    int firstChildTop = 0;
                    if (recyclerView.getChildCount() > 0) {
                        View firstChild = recyclerView.getChildAt(0);
                        if (firstChild == null) {
                            return false;
                        }
                        // 处理item高度超过一屏幕时的情况
                        if (firstChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                            return !recyclerView.canScrollVertically(-1);
                        }
                        RecyclerView.LayoutParams childParams = (RecyclerView.LayoutParams) firstChild.getLayoutParams();
                        firstChildTop = firstChild.getTop() - childParams.topMargin -
                                getRecyclerViewItemTopInset(childParams) - recyclerView.getPaddingTop();
                    }
                    if (((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0) {
                        return true;
                    }
                } else if (manager instanceof StaggeredGridLayoutManager) {
                    int[] out = ((StaggeredGridLayoutManager) manager).findFirstCompletelyVisibleItemPositions((int[]) null);
                    if (out[0] < 1) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private static int getRecyclerViewItemTopInset(RecyclerView.LayoutParams layoutParams) {
        try {
            Field field = RecyclerView.LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            Rect rect = (Rect) field.get(layoutParams);
            return rect.top;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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

    public static boolean isRecyclerViewToBottom(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager != null && manager.getItemCount() > 0) {
                if (manager instanceof LinearLayoutManager) {
                    int lastVisiblePosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int childCount = manager.getItemCount();
                    boolean isLast = lastVisiblePosition == childCount - 1;
                    boolean cannotScroll = !recyclerView.canScrollVertically(1);
                    return isLast && cannotScroll;
                } else if (manager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
                    int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
                    int lastPosition = layoutManager.getItemCount() - 1;
                    for (int position : out) {
                        if (position == lastPosition) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 视图必须可以滚动
     * 即view.getChildAt(0).getHeight() 大于 view.getMeasuredHeight()
     */
    public static boolean isViewGroupToBottom(ViewGroup view) {
        if (view == null) {
            return false;
        }
        int childCount = view.getChildCount();
        if (childCount == 0) {
            return true;
        } else if (childCount == 1) {
            return view.getScrollY() >= view.getChildAt(0).getMeasuredHeight() - view.getHeight();
        }
        View child = null;
        while (childCount > 0) {
            child = view.getChildAt(childCount - 1);
            if (child != null && child.getVisibility() != View.GONE) {
                break;
            } else {
                child = null;
                childCount--;
            }
        }
        if (child != null) {
            Log.e("TAG", "child.getBottom=" + child.getBottom() + ";view.getBottom=" + view.getBottom() + ";canScrollVertically=" + view.canScrollVertically(-1) + ";getScrollY=" + view.getScrollY());

            return child.getBottom() <= view.getMeasuredHeight() && !view.canScrollVertically(-1);
        }
        return true;
    }

    public static boolean isWebViewToBottom(WebView view) {
        return view != null && (float) view.getScrollY() >= (float) view.getContentHeight() * view.getScale() - (float) view.getMeasuredHeight();
    }

}
