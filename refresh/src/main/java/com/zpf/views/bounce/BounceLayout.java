package com.zpf.views.bounce;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Created by ZPF on 2022/3/15.
 */
public class BounceLayout extends ViewGroup {
    private int state = BounceLayoutState.INIT;// 当前状态
    private int type = BounceLayoutType.ONLY_STRETCHY;//当前类型
    private final BothEndsLayout headLayout;
    private final BothEndsLayout footLayout;
    protected View bodyLayout;
    private float pullY = 0f;//Y轴变化量
    private float lastX = -1f;
    private float lastY = -1f;
    protected int maxRollBackTime = 480;//最大回弹时间
    protected int finishDelayed = 500;//完成后停顿时间
    private boolean multiTouch = false;//多点触控过滤
    private OnLoadListener mListener;//监听
    private IViewPullChecker handler;
    private int lastChildAction = MotionEvent.ACTION_CANCEL;
    private Animator rollBackAnimator;//回滚动画

    public BounceLayout(Context context) {
        this(context, null, 0);
    }

    public BounceLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BounceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        headLayout = new BothEndsLayout(context);
        footLayout = new BothEndsLayout(context, true);
        initConfig(context, attrs, defStyleAttr);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        //不支持padding
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        //不支持padding
    }

    protected void initConfig(Context context, AttributeSet attrs, int defStyleAttr) {
        super.setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (bodyLayout != null) {
            removeAllViews();
            addView(headLayout);
            addView(footLayout);
            addView(bodyLayout);
            return;
        }
        int childCount = getChildCount();
        if (childCount == 1) {
            bodyLayout = getChildAt(0);
            addView(headLayout);
            addView(footLayout);
        } else if (childCount == 2) {
            View headView = getChildAt(0);
            bodyLayout = getChildAt(1);
            removeView(headView);
            if (headView instanceof IBothEndsViewHandler) {
                headLayout.setContentView((IBothEndsViewHandler) headView);
            } else if (headView != null) {
                headLayout.addView(headView);
            }
            addView(headLayout);
            addView(footLayout);
        } else if (childCount == 3) {
            View headView = getChildAt(0);
            bodyLayout = getChildAt(1);
            View footView = getChildAt(2);
            removeView(headView);
            if (headView instanceof IBothEndsViewHandler) {
                headLayout.setContentView((IBothEndsViewHandler) headView);
            } else if (headView != null) {
                headLayout.addView(headView);
            }
            removeView(footView);
            if (footView instanceof IBothEndsViewHandler) {
                footLayout.setContentView((IBothEndsViewHandler) footView);
            } else if (footView != null) {
                footLayout.addView(footView);
            }
            addView(headLayout);
            addView(footLayout);
        } else {
            throw new IllegalStateException("不支持子控件数量：" + childCount);
        }
        headLayout.setType(type);
        footLayout.setType(type);
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
                final LayoutParams lp = child.getLayoutParams();
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
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
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
                if (rollBackAnimator != null) {
                    rollBackAnimator.cancel();
                }
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
                if (Math.abs(dy) < Math.abs(dx) || type == BounceLayoutType.NO_STRETCHY) {
                    if (lastChildAction != MotionEvent.ACTION_DOWN && lastChildAction != MotionEvent.ACTION_MOVE) {
                        ev.setAction(MotionEvent.ACTION_DOWN);
                    }
                    break;
                }
                boolean changePullY = false;
                // 根据下拉距离改变比例，手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
                float radio = (float) (1 + Math.tan(Math.PI * Math.abs(pullY) / getMeasuredHeight()));
                if (dy > 0) {
                    //检查当前是否可以下拉
                    if (pullY != 0 || isContentToTop()) {
                        if (pullY < 0 || state == BounceLayoutState.LOADING) {
                            pullY = Math.min(0, pullY + dy / radio);
                        } else {
                            pullY = pullY + dy / radio;
                        }
                        changePullY = true;
                    }
                } else {
                    //检查当前是否可以上拉
                    if (pullY != 0 || isContentToBottom()) {
                        if (pullY > 0 || state == BounceLayoutState.REFRESHING) {
                            pullY = Math.max(0, pullY + dy / radio);
                        } else {
                            pullY = pullY + dy / radio;
                        }
                        changePullY = true;
                    }
                }
                if (changePullY) {
                    float maxPullY = 0.6f * getMeasuredHeight();
                    if (pullY > maxPullY) {
                        pullY = maxPullY;
                    } else if (pullY < -maxPullY) {
                        pullY = -maxPullY;
                    }
                    layoutChildren();
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    // 根据pullY更新状态
                    if (state != BounceLayoutState.REFRESHING && state != BounceLayoutState.LOADING
                            && state != BounceLayoutState.SUCCEED && state != BounceLayoutState.FAIL) {
                        if (pullY > 0) {
                            if (pullY <= headLayout.getDistHeight()) {
                                changeState(BounceLayoutState.INIT);
                            } else {
                                changeState(BounceLayoutState.TO_REFRESH);
                            }
                        } else if (pullY < 0) {
                            if (-pullY <= footLayout.getDistHeight()) {
                                changeState(BounceLayoutState.INIT);
                            } else {
                                changeState(BounceLayoutState.TO_LOAD);
                            }
                        } else {
                            changeState(BounceLayoutState.INIT);
                        }
                    }
                } else if (lastChildAction != MotionEvent.ACTION_DOWN && lastChildAction != MotionEvent.ACTION_MOVE) {
                    ev.setAction(MotionEvent.ACTION_DOWN);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                lastX = -1;
                lastY = -1;
                if (state == BounceLayoutState.REFRESHING) {
                    if (pullY > (headLayout.getDistHeight() * 0.5f)) {
                        rollBack(BounceLayoutState.REFRESHING, headLayout.getDistHeight(), 0);
                    } else {
                        rollBack(BounceLayoutState.REFRESHING, 0, 0);
                    }
                } else if (state == BounceLayoutState.LOADING) {
                    if (-pullY > (footLayout.getDistHeight() * 0.5f)) {
                        rollBack(BounceLayoutState.LOADING, -footLayout.getDistHeight(), 0);
                    } else {
                        rollBack(BounceLayoutState.LOADING, 0, 0);
                    }
                } else if ((state == BounceLayoutState.TO_REFRESH) &&
                        (type == BounceLayoutType.ONLY_PULL_DOWN || type == BounceLayoutType.BOTH_UP_DOWN)) {
                    rollBack(BounceLayoutState.REFRESHING, headLayout.getDistHeight(), 0);
                } else if ((state == BounceLayoutState.TO_LOAD) &&
                        (type == BounceLayoutType.ONLY_PULL_UP || type == BounceLayoutType.BOTH_UP_DOWN)) {
                    rollBack(BounceLayoutState.LOADING, -footLayout.getDistHeight(), 0);
                } else {
                    rollBack(BounceLayoutState.INIT, 0, 0);
                }
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        lastChildAction = ev.getAction();
        return true;
    }

    public boolean isContentToBottom() {
        return getCheckListener().checkPullUp(bodyLayout);
    }

    public boolean isContentToTop() {
        return getCheckListener().checkPullDown(bodyLayout);
    }

    public void setListener(OnLoadListener mListener) {
        this.mListener = mListener;
    }

    public void setBodyLayout(View bodyLayout) {
        this.bodyLayout = bodyLayout;
    }

    public void setHeadAndFoot(IBothEndsViewHandler head, IBothEndsViewHandler foot) {
        headLayout.setContentView(head);
        if (head != null) {
            head.onTypeChange(type);
        }
        footLayout.setContentView(foot);
        if (foot != null) {
            foot.onTypeChange(type);
        }
    }

    public void setType(@BounceLayoutType int type) {
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

    public void setMaxRollBackTime(int maxRollBackTime) {
        this.maxRollBackTime = maxRollBackTime;
    }

    public void setFinishDelayedTime(int delayed) {
        this.finishDelayed = Math.max(delayed, 0);
    }

    public void setResult(boolean success) {
        if (this.state == BounceLayoutState.INIT) {
            return;
        }
        changeState(success ? BounceLayoutState.SUCCEED : BounceLayoutState.FAIL);
    }

    private void layoutChildren() {
        MarginLayoutParams lp = (MarginLayoutParams) bodyLayout.getLayoutParams();
        bodyLayout.layout(lp.leftMargin, (int) (pullY + lp.topMargin),
                getMeasuredWidth() - lp.rightMargin, (int) (pullY + getMeasuredHeight() - lp.bottomMargin));
        if (type == BounceLayoutType.NO_STRETCHY) {
            headLayout.setVisibility(View.GONE);
            footLayout.setVisibility(View.GONE);
            return;
        } else {
            headLayout.setVisibility(View.VISIBLE);
            footLayout.setVisibility(View.VISIBLE);
        }
        if (headLayout.getDistHeight() > 0) {
            headLayout.layout(0, (int) (pullY - headLayout.getMeasuredHeight()), getMeasuredWidth(),
                    (int) (pullY));
        }
        if (footLayout.getDistHeight() > 0) {
            footLayout.layout(0, (int) (pullY + getMeasuredHeight()), getMeasuredWidth(),
                    (int) (pullY + getMeasuredHeight() + footLayout.getMeasuredHeight()));
        }
    }

    private IViewPullChecker getCheckListener() {
        if (handler == null) {
            handler = DefViewPullCheckerImpl.get();
        }
        return handler;
    }

    private void rollBack(final int endState, final float endY, long delay) {
        if (rollBackAnimator != null) {
            rollBackAnimator.cancel();
        }
        if (pullY == endY) {
            changeState(endState);
            return;
        }
        float maxPullY = 0.6f * getMeasuredHeight();
        long duration = (long) (maxRollBackTime * Math.abs(pullY) / maxPullY);
        final ValueAnimator animator = ValueAnimator.ofFloat(pullY, endY);
        animator.setDuration(Math.max(duration, 32));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pullY = (float) animator.getAnimatedValue();
                layoutChildren();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rollBackAnimator = null;
                if (pullY == endY) {
                    changeState(endState);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                rollBackAnimator = null;

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setStartDelay(delay);
        animator.start();
        rollBackAnimator = animator;
    }

    private void changeState(@BounceLayoutState int state) {
        if (!(this.state == BounceLayoutState.TO_LOAD || this.state == BounceLayoutState.LOADING)
                && !(state == BounceLayoutState.TO_LOAD || state == BounceLayoutState.LOADING)) {
            headLayout.changeState(state);
        }
        if (!(this.state == BounceLayoutState.TO_REFRESH || this.state == BounceLayoutState.REFRESHING)
                && !(state == BounceLayoutState.TO_REFRESH || state == BounceLayoutState.REFRESHING)) {
            footLayout.changeState(state);
        }
        int oldState = this.state;
        this.state = state;
        if (state == BounceLayoutState.REFRESHING) {
            if (oldState != state && mListener != null) {
                mListener.onRefresh();
            }
        } else if (state == BounceLayoutState.LOADING) {
            if (oldState != state && mListener != null) {
                mListener.onLoadMore();
            }
        } else if (state == BounceLayoutState.SUCCEED || state == BounceLayoutState.FAIL) {
            rollBack(BounceLayoutState.INIT, 0, finishDelayed);
        }
    }
}