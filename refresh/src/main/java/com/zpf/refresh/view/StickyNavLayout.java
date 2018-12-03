package com.zpf.refresh.view;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private int mTouchSlop;
    private int mMaximumVelocity;
    private int mMinimumVelocity;

    private boolean mIsInControl = true;

    private float mLastDispatchY;
    private float mLastTouchY;
    private int mContentHeight;

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        mOverScroller = new OverScroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    public void setOrientation(int orientation) {
        if (VERTICAL == orientation) {
            super.setOrientation(VERTICAL);
        }
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
        measureChild(mContentView, widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec) - getNavViewHeight(), MeasureSpec.EXACTLY));
        mContentHeight = mContentView.getMeasuredHeight();
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

    /**
     * 获取导航视图的高度，包括topMargin和bottomMargin
     */
    public int getNavViewHeight() {
        MarginLayoutParams layoutParams = (MarginLayoutParams) mNavView.getLayoutParams();
        return mNavView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    public int getContentHeight() {
        return mContentHeight;
    }

    public int getAllChildHeight() {
        return mContentHeight + getNavViewHeight() + getHeaderViewHeight();
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
        float currentTouchY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDispatchY = currentTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float differentY = currentTouchY - mLastDispatchY;
                mLastDispatchY = currentTouchY;
                if (isContentViewToTop() && isHeaderViewCompleteInvisible()) {
                    if (differentY >= 0 && !mIsInControl) {
                        mIsInControl = true;
                        return resetDispatchTouchEvent(ev);
                    }
                    if (differentY <= 0 && mIsInControl) {
                        mIsInControl = false;
                        return resetDispatchTouchEvent(ev);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean resetDispatchTouchEvent(MotionEvent ev) {
        MotionEvent newEvent = MotionEvent.obtain(ev);
        ev.setAction(MotionEvent.ACTION_CANCEL);
        dispatchTouchEvent(ev);
        newEvent.setAction(MotionEvent.ACTION_DOWN);
        return dispatchTouchEvent(newEvent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float currentTouchY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float differentY = currentTouchY - mLastTouchY;
                if (Math.abs(differentY) > mTouchSlop) {
                    if (!isHeaderViewCompleteInvisible() || (isContentViewToTop() && isHeaderViewCompleteInvisible() && mIsInControl)) {
                        mLastTouchY = currentTouchY;
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        float currentTouchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }

                mLastTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float differentY = currentTouchY - mLastTouchY;
                if (isContentViewToTop() && Math.abs(differentY) > 0) {
                    scrollBy(0, (int) -differentY);
                }
                mLastTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) mVelocityTracker.getYVelocity();
                if ((Math.abs(initialVelocity) > mMinimumVelocity) && isContentViewToTop()) {
                    fling(-initialVelocity);
                }
                recycleVelocityTracker();
                break;
        }
        return true;
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
        } else {
            return ViewBorderUtil.isViewToTop(view);
        }
    }

    private boolean checkViewToBottom(View view) {
        if (view == null) {
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
        } else
            return view instanceof ViewGroup && ViewBorderUtil.isViewGroupToBottom((ViewGroup) view);
    }

}
