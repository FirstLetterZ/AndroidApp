package com.zpf.refresh.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.refresh.util.BaseViewStateCheckImpl;
import com.zpf.refresh.util.HeadFootImpl;
import com.zpf.refresh.util.OnRefreshListener;
import com.zpf.refresh.util.RefreshLayoutState;
import com.zpf.refresh.util.RefreshLayoutType;
import com.zpf.refresh.util.ViewStateCheckListener;

/**
 * @author Created by ZPF on 2021/3/2.
 */
public class LoadLayout extends ViewGroup {
    private int state = RefreshLayoutState.INIT;// 当前状态
    private int type = RefreshLayoutType.ONLY_STRETCHY;//当前类型
    private final HeadFootLayout headLayout;
    private final HeadFootLayout footLayout;
    private View bodyLayout;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;
    private float pullY = 0f;//Y轴变化量
    private float lastX = -1f;
    private float lastY = -1f;
    protected int loadDelayed = 500;//完成后停顿时间
    protected int refreshDelayed = 800;//完成后停顿时间
    private boolean multiTouch = false;//多点触控过滤
    private boolean isTouching = false;//触摸后停止回弹
    private OnRefreshListener mListener;//监听
    private ViewStateCheckListener checkListener;
    private int lastChildAction = MotionEvent.ACTION_CANCEL;
    private long lastLayoutTime = 0;

    public LoadLayout(Context context) {
        this(context, null, 0);
    }

    public LoadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        headLayout = new HeadFootLayout(context);
        headLayout.setHeadFootInterface(new HeadFootImpl());
        headLayout.setType(type);
        footLayout = new HeadFootLayout(context, true);
        footLayout.setHeadFootInterface(new HeadFootImpl());
        footLayout.setType(type);
        super.setPadding(0, 0, 0, 0);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        //不支持padding
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        //不支持padding
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (bodyLayout != null) {
            removeAllViews();
            addView(headLayout);
            addView(footLayout);
            addView(bodyLayout);
        } else if (childCount == 1) {
            bodyLayout = getChildAt(0);
            addView(headLayout);
            addView(footLayout);
        } else {
            throw new IllegalStateException("只能有一个控件");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childMarginLeft = 0;
                int childMarginRight = 0;
                int childMarginTop = 0;
                int childMarginBottom = 0;
                final ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp instanceof MarginLayoutParams) {
                    childMarginLeft = ((MarginLayoutParams) lp).leftMargin;
                    childMarginRight = ((MarginLayoutParams) lp).rightMargin;
                    childMarginTop = ((MarginLayoutParams) lp).topMargin;
                    childMarginBottom = ((MarginLayoutParams) lp).bottomMargin;
                }
                final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        childMarginLeft + childMarginRight, lp.width);
                final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        childMarginTop + childMarginBottom, lp.height);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof MarginLayoutParams) {
            return (MarginLayoutParams) p;
        }
        return new MarginLayoutParams(p);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getRawX();
                lastY = ev.getRawY();
                multiTouch = false;
                isTouching = true;
                removeCallbacks(backRunnable);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 过滤多点触碰
                multiTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (multiTouch) {
                    break;
                }
                float dx = ev.getRawX() - lastX;
                float dy = ev.getRawY() - lastY;
                lastX = ev.getRawX();
                lastY = ev.getRawY();
                if (Math.abs(dy) < Math.abs(dx) || type == RefreshLayoutType.NO_STRETCHY) {
                    if (lastChildAction != MotionEvent.ACTION_DOWN && lastChildAction != MotionEvent.ACTION_MOVE) {
                        ev.setAction(MotionEvent.ACTION_DOWN);
                    }
                    break;
                }
                boolean changePullY = false;
                // 根据下拉距离改变比例*
                radio = (float) (1 + Math.tan(Math.PI * Math.abs(pullY) / getMeasuredHeight()));
//                Log.e("TAG_ZPF", "dispatchTouchEvent==>pullY=" + pullY + ";dy=" + dy + ";state=" + state);
                if (dy > 0) {
                    //检查当前是否可以下拉
                    if (pullY != 0 || isContentToTop()) {
                        if (pullY < 0 || state == RefreshLayoutState.LOADING) {
                            pullY = Math.min(0, pullY + dy / radio);
                        } else {
                            pullY = pullY + dy / radio;
                        }
                        changePullY = true;
                    }
                } else {
                    //检查当前是否可以上拉
                    if (pullY != 0 || isContentToBottom()) {
                        if (pullY > 0 || state == RefreshLayoutState.REFRESHING) {
                            pullY = Math.max(0, pullY + dy / radio);
                        } else {
                            pullY = pullY + dy / radio;
                        }
                        changePullY = true;
                    }
                }
                if (changePullY) {
                    float maxPullY = 0.8f * getMeasuredHeight();
                    if (pullY > maxPullY) {
                        pullY = maxPullY;
                    } else if (pullY < -maxPullY) {
                        pullY = -maxPullY;
                    }
                    // 根据pullY更新状态
                    if (pullY > 0) {
                        if (pullY <= headLayout.getDistHeight()
                                && (state == RefreshLayoutState.TO_REFRESH || state == RefreshLayoutState.DONE)) {
                            // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                            changeState(RefreshLayoutState.INIT);
                        } else if (pullY > headLayout.getDistHeight() && state == RefreshLayoutState.INIT) {
                            // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                            changeState(RefreshLayoutState.TO_REFRESH);
                        }
                    } else if (pullY < 0) {
                        if (-pullY <= footLayout.getDistHeight()
                                && (state == RefreshLayoutState.TO_LOAD || state == RefreshLayoutState.DONE)) {
                            changeState(RefreshLayoutState.INIT);
                        } else if (-pullY > footLayout.getDistHeight() && state == RefreshLayoutState.INIT) {
                            changeState(RefreshLayoutState.TO_LOAD);
                        }
                    } else if (state != RefreshLayoutState.REFRESHING && state != RefreshLayoutState.LOADING) {
                        changeState(RefreshLayoutState.INIT);
                    }
                    layoutChildren();
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                } else if (lastChildAction != MotionEvent.ACTION_DOWN && lastChildAction != MotionEvent.ACTION_MOVE) {
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                isTouching = false;
                lastX = -1;
                lastY = -1;
                //根据pullY更新状态
                if (state == RefreshLayoutState.TO_REFRESH &&
                        (type == RefreshLayoutType.ONLY_PULL_DOWN || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                    changeState(RefreshLayoutState.REFRESHING);
                    // 刷新操作
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                } else if (state == RefreshLayoutState.TO_LOAD &&
                        (type == RefreshLayoutType.ONLY_PULL_UP || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                    changeState(RefreshLayoutState.LOADING);
                    // 加载操作
                    if (mListener != null) {
                        mListener.onLoadMore();
                    }
                }
                scrollBack(0);
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        lastChildAction = ev.getAction();
        return true;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public boolean isContentToBottom() {
        return getCheckListener().checkPullUp(bodyLayout);
    }

    public boolean isContentToTop() {
        return getCheckListener().checkPullDown(bodyLayout);
    }

    public void setListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    public void setType(@RefreshLayoutType int type) {
        this.type = type;
        headLayout.setType(type);
        footLayout.setType(type);
    }

    public int getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    //设置刷新和加载布局停留时间
    public void setDelayedTime(int refreshDelayed, int loadDelayed) {
        this.refreshDelayed = Math.max(refreshDelayed, 0);
        this.loadDelayed = Math.max(loadDelayed, 0);
    }

    public void setResult(boolean success) {
        if (this.state == RefreshLayoutState.INIT || this.state == RefreshLayoutState.DONE) {
            return;
        }
        changeState(success ? RefreshLayoutState.SUCCEED : RefreshLayoutState.FAIL);
    }

    private void scrollBack() {
        scrollBack(16 + lastLayoutTime - System.currentTimeMillis());
    }

    private void scrollBack(long delay) {
        if (delay > 14) {
            delay = 14;
        } else if (delay < 0) {
            delay = 0;
        }
        removeCallbacks(backRunnable);
        postDelayed(backRunnable, delay);
    }

    private final Runnable backRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTouching || pullY == 0) {
                return;
            }
            float end = 0;
            if (state == RefreshLayoutState.REFRESHING && pullY >= headLayout.getDistHeight()) {
                end = headLayout.getDistHeight();
            } else if (state == RefreshLayoutState.LOADING && pullY <= -headLayout.getDistHeight()) {
                end = -footLayout.getDistHeight();
            }
            radio = (float) Math.tan(Math.PI * Math.abs(pullY) / getMeasuredHeight()) + 2;
            if (pullY > 0) {
                pullY = Math.max(end, pullY - 8 * radio);
            } else {
                pullY = Math.min(end, pullY + 8 * radio);
            }
            layoutChildren();
            if (pullY != end) {
                scrollBack();
            } else if (state != RefreshLayoutState.REFRESHING && state != RefreshLayoutState.LOADING) {
                changeState(RefreshLayoutState.INIT);
            }
        }
    };

    private void layoutChildren() {
        MarginLayoutParams lp = (MarginLayoutParams) bodyLayout.getLayoutParams();
        bodyLayout.layout(lp.leftMargin, (int) (pullY + lp.topMargin),
                getMeasuredWidth() - lp.rightMargin, (int) (pullY + getMeasuredHeight() - lp.bottomMargin));
        if (type == RefreshLayoutType.NO_STRETCHY) {
            lastLayoutTime = System.currentTimeMillis();
            return;
        }
        if (headLayout.getDistHeight() > 0) {
            headLayout.layout(0, (int) (pullY - headLayout.getMeasuredHeight()), getMeasuredWidth(),
                    (int) (pullY));
        }
        if (footLayout.getDistHeight() > 0) {
            footLayout.layout(0, (int) (pullY + getMeasuredHeight()), getMeasuredWidth(),
                    (int) (pullY + getMeasuredHeight() + footLayout.getMeasuredHeight()));
        }
        lastLayoutTime = System.currentTimeMillis();
    }

    private ViewStateCheckListener getCheckListener() {
        if (checkListener == null) {
            checkListener = BaseViewStateCheckImpl.get();
        }
        return checkListener;
    }

    private void changeState(@RefreshLayoutState int state) {
        if (!(this.state == RefreshLayoutState.TO_LOAD || this.state == RefreshLayoutState.LOADING)
                && !(state == RefreshLayoutState.TO_LOAD || state == RefreshLayoutState.LOADING)) {
            headLayout.changeState(state);
        }
        if (!(this.state == RefreshLayoutState.TO_REFRESH || this.state == RefreshLayoutState.REFRESHING)
                && !(state == RefreshLayoutState.TO_REFRESH || state == RefreshLayoutState.REFRESHING)) {
            footLayout.changeState(state);
        }
        if (state == RefreshLayoutState.SUCCEED || state == RefreshLayoutState.FAIL) {
            long delay = 10;
            if (this.state == RefreshLayoutState.LOADING) {
                delay = loadDelayed;
            } else if (this.state == RefreshLayoutState.REFRESHING) {
                delay = refreshDelayed;
            }
            this.state = RefreshLayoutState.DONE;
            scrollBack(delay);
        } else {
            this.state = state;
        }
    }
}
