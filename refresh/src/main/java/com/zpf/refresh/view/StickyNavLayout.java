package com.zpf.refresh.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.zpf.api.PackedLayoutInterface;
import com.zpf.refresh.util.ViewBorderUtil;

public class StickyNavLayout extends LinearLayout {
    private View mHeaderView;
    private View mNavView;
    private View mContentView;
    private View mNestedContentView;

    private OverScroller mOverScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    boolean passChild = false;

    private float lastY = -1;

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mOverScroller = new OverScroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(VERTICAL);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 3) {
            throw new IllegalStateException("必须有且只有三个子控件");
        }
        mHeaderView = getChildAt(0);
        mNavView = getChildAt(1);
        mContentView = getChildAt(2);
        if (mContentView instanceof ViewPager) {
            ((ViewPager) mContentView).addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    getNestedContentView((ViewPager) mContentView);
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MarginLayoutParams layoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
        measureChild(mContentView, widthMeasureSpec - layoutParams.leftMargin - layoutParams.rightMargin,
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) - getNavViewHeight()
                        - layoutParams.topMargin - layoutParams.bottomMargin, MeasureSpec.EXACTLY));
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(0, mOverScroller.getCurrY());
            invalidate();
        }
    }

    public void fling(int velocityY) {
        mOverScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, getHeaderViewHeight());
        invalidate();
    }

    public void hideHeader() {
        scrollTo(0, getHeaderViewHeight());
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        int headerViewHeight = getHeaderViewHeight();
        if (y > headerViewHeight) {
            y = headerViewHeight;
        }

        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    /**
     * 获取头部视图高度，包括topMargin和bottomMargin
     */
    public int getHeaderViewHeight() {
        MarginLayoutParams layoutParams = (MarginLayoutParams) mHeaderView.getLayoutParams();
        return mHeaderView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    public int getContentViewHeight() {
        MarginLayoutParams layoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
        return mContentView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    /**
     * 获取导航视图的高度，包括topMargin和bottomMargin
     */
    public int getNavViewHeight() {
        MarginLayoutParams layoutParams = (MarginLayoutParams) mNavView.getLayoutParams();
        return mNavView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    public int getAllChildHeight() {
        return getContentViewHeight() + getNavViewHeight() + getHeaderViewHeight();
    }

    /**
     * 头部视图是否已经完全显示
     */
    private boolean isHeaderViewCompleteVisible() {
        // 0表示x，1表示y
        int[] location = new int[2];
        getLocationOnScreen(location);
        int contentOnScreenTopY = location[1] + getPaddingTop();

        mHeaderView.getLocationOnScreen(location);
        MarginLayoutParams params = (MarginLayoutParams) mHeaderView.getLayoutParams();
        int navViewTopOnScreenY = location[1] - params.topMargin;
        return navViewTopOnScreenY == contentOnScreenTopY;
    }

    /**
     * 头部视图是否已经完全隐藏
     */
    private boolean isHeaderViewCompleteInvisible() {
        // 0表示x，1表示y
        int[] location = new int[2];
        getLocationOnScreen(location);
        int contentOnScreenTopY = location[1] + getPaddingTop();

        mNavView.getLocationOnScreen(location);
        MarginLayoutParams params = (MarginLayoutParams) mNavView.getLayoutParams();
        int navViewTopOnScreenY = location[1] - params.topMargin;
        return navViewTopOnScreenY == contentOnScreenTopY;
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        float dY;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getRawY();
                mVelocityTracker.addMovement(ev);
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                passChild = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getRawY();
                dY = currentY - lastY;
                if (Math.abs(dY) > 4) {
                    lastY = currentY;
                    boolean isContentViewToTop = isContentViewToTop();
                    boolean lastPassState = passChild;
                    if (dY > 0) {
                        passChild = !isContentViewToTop || isHeaderViewCompleteVisible();
                    } else {
                        passChild = isHeaderViewCompleteInvisible();
                    }
                    if (passChild) {
                        if (!lastPassState) {
                            ev.setAction(MotionEvent.ACTION_DOWN);
                        }
                    } else {
                        scrollBy(0, (int) -dY);
                        mVelocityTracker.addMovement(ev);
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else {
                    passChild = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                lastY = -1;
                if (!passChild) {
                    mVelocityTracker.addMovement(ev);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity();
                    if ((Math.abs(initialVelocity) > mMinimumVelocity) && isContentViewToTop()) {
                        fling(-initialVelocity);
                    }
                    recycleVelocityTracker();
                }
                dispatchTouchEventToChild(ev);
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (!passChild) {
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(ev);
                        recycleVelocityTracker();
                    }
                    if (!mOverScroller.isFinished()) {
                        mOverScroller.abortAnimation();
                    }
                }
                break;
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void dispatchTouchEventToChild(MotionEvent ev) {
        float yAxis = ev.getRawY();
        float xAxis = ev.getRawX();
        Rect rect = getViewRect(mContentView);
        if (yAxis >= rect.top && yAxis <= rect.bottom && xAxis >= rect.left
                && xAxis <= rect.right) {
            mContentView.dispatchTouchEvent(ev);
            return;
        }
        rect = getViewRect(mNavView);
        if (yAxis >= rect.top && yAxis <= rect.bottom && xAxis >= rect.left
                && xAxis <= rect.right) {
            mNavView.dispatchTouchEvent(ev);
            return;
        }
        rect = getViewRect(mHeaderView);
        if (yAxis >= rect.top && yAxis <= rect.bottom && xAxis >= rect.left
                && xAxis <= rect.right) {
            mHeaderView.dispatchTouchEvent(ev);
        }
    }

    private Rect getViewRect(View view) {
        MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect rect = new Rect();
        rect.left = location[0] - params.leftMargin;
        rect.top = location[1] - params.topMargin;
        rect.right = rect.left + view.getMeasuredWidth() + params.rightMargin;
        rect.bottom = rect.top + view.getMeasuredHeight() + params.bottomMargin;
        return rect;
    }

    public boolean isContentViewToTop() {
        return checkViewToTop(mContentView);
    }

    public boolean isContentViewToBottom() {
        return checkViewToBottom(mContentView);
    }

    /**
     * 重新获取嵌套的内容视图
     */
    private void getNestedContentView(ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof FragmentPagerAdapter || adapter instanceof FragmentStatePagerAdapter) {
            Fragment item = (Fragment) adapter.instantiateItem(viewPager, currentItem);
            mNestedContentView = item.getView();
            if (!isHeaderViewCompleteInvisible()) {
                if (mNestedContentView instanceof AbsListView) {
                    ((AbsListView) mNestedContentView).setSelection(0);
                } else if (mNestedContentView instanceof RecyclerView) {
                    ((RecyclerView) mNestedContentView).scrollToPosition(0);
                } else if (mNestedContentView instanceof ScrollView) {
                    mNestedContentView.scrollTo(mNestedContentView.getScrollX(), 0);
                } else if (mNestedContentView instanceof WebView) {
                    mNestedContentView.scrollTo(mNestedContentView.getScrollX(), 0);
                }
            }
        } else {
            throw new IllegalStateException("第三个子控件为ViewPager时，其adapter必须是FragmentPagerAdapter或者FragmentStatePagerAdapter");
        }
    }

    private boolean checkViewToTop(View view) {
        if (view == null) {
            return false;
        } else if (view instanceof ViewPager) {
            if (mNestedContentView == null) {
                getNestedContentView((ViewPager) view);
            }
            return checkViewToTop(mNestedContentView);
        } else if (view instanceof AbsListView) {
            return ViewBorderUtil.isAbsListViewToTop((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return ViewBorderUtil.isRecyclerViewToTop((RecyclerView) view);
        } else if (view instanceof PackedLayoutInterface) {
            return checkViewToTop(((PackedLayoutInterface) view).getCurrentChild());
        } else if (view instanceof RefreshLayout) {
            return ((RefreshLayout) view).checkPullDown();
        } else {
            return ViewBorderUtil.isViewToTop(view);
        }
    }

    private boolean checkViewToBottom(View view) {
        if (view == null) {
            return false;
        } else if (!isHeaderViewCompleteInvisible()) {
            return false;
        } else if (view instanceof ViewPager) {
            if (mNestedContentView == null) {
                getNestedContentView((ViewPager) view);
            }
            return checkViewToBottom(mNestedContentView);
        } else if (view instanceof AbsListView) {
            return ViewBorderUtil.isAbsListViewToBottom((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return ViewBorderUtil.isRecyclerViewToBottom((RecyclerView) view);
        } else if (view instanceof WebView) {
            return ViewBorderUtil.isWebViewToBottom((WebView) view);
        } else if (view instanceof PackedLayoutInterface) {
            return checkViewToBottom(((PackedLayoutInterface) view).getCurrentChild());
        } else if (view instanceof RefreshLayout) {
            return ((RefreshLayout) view).checkPullUp();
        } else
            return view instanceof ViewGroup && ViewBorderUtil.isViewGroupToBottom((ViewGroup) view);
    }

}
