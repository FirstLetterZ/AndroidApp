package com.zpf.refresh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.zpf.refresh.util.BaseViewStateCheckImpl;
import com.zpf.refresh.util.HeadFootImpl;
import com.zpf.refresh.util.RefreshLayoutState;
import com.zpf.refresh.util.RefreshLayoutType;
import com.zpf.refresh.util.ViewStateCheckListener;

/**
 * @author Created by ZPF on 2021/3/2.
 */
public class LoadLayout extends FrameLayout {
    private int state = RefreshLayoutState.INIT;// 当前状态
    private int type = RefreshLayoutType.BOTH_UP_DOWN;//当前类型
    private HeadFootLayout headLayout;
    private HeadFootLayout footLayout;
    private View bodyLayout;
    private float pullY = 0f;
    private float lastX = -1f;
    private float lastY = -1f;
    private boolean multiTouch = false;
    private boolean isTouching = false;
    private ViewStateCheckListener checkListener;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("ZPF", "onLayout==>left=" + l + ";top=" + t + ";right=" + r + ";bottom=" + b);
        layoutChildren();
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
                if (!multiTouch) {
                    float dx = ev.getRawX() - lastX;
                    float dy = ev.getRawY() - lastY;
                    lastX = ev.getRawX();
                    lastY = ev.getRawY();
                    if (Math.abs(dy) > Math.abs(dx) && type != RefreshLayoutType.NO_STRETCHY) {
                        Log.e("ZPF", "ACTION_MOVE222==>dy=" + dy + ";pullY=" + pullY
                                + "; isContentToTop=" + isContentToTop() + "; state=" + state);
                        //检查当前是否可以下拉
                        boolean changePullY = (pullY != 0 || (dy > 0 && isContentToTop() && state != RefreshLayoutState.LOADING)
                                || (dy < 0 && isContentToBottom() && state != RefreshLayoutState.REFRESHING));
                        Log.e("ZPF", "ACTION_MOVE222==>changePullY=" + changePullY);
                        if (changePullY) {
                            //TODO 根据下拉距离确定改变比例
                            pullY = pullY + dy;
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
                            } else {
                                changeState(RefreshLayoutState.INIT);
                            }
                            layoutChildren();
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                isTouching = false;
                lastX = -1;
                lastY = -1;
                //根据pullY更新状态
                //TODO  触发监听
                if (state == RefreshLayoutState.TO_REFRESH &&
                        (type == RefreshLayoutType.ONLY_PULL_DOWN || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                    changeState(RefreshLayoutState.REFRESHING);
                    // 刷新操作
//                    if (mListener != null) {
//                        mListener.onRefresh();
//                    }
                } else if (state == RefreshLayoutState.TO_LOAD &&
                        (type == RefreshLayoutType.ONLY_PULL_UP || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                    changeState(RefreshLayoutState.LOADING);
                    // 加载操作
//                    if (mListener != null) {
//                        mListener.onLoadMore();
//                    }
                } else {
                    scrollBack();
                }
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    public boolean isContentToBottom() {
        return getCheckListener().checkPullUp(bodyLayout);
    }

    public boolean isContentToTop() {
        return getCheckListener().checkPullDown(bodyLayout);
    }

    private void scrollBack() {
        removeCallbacks(backRunnable);
        postDelayed(backRunnable, 10);
    }

    private final Runnable backRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTouching || pullY == 0) {
                return;
            }
            if (pullY > 0) {
//                if (state == RefreshLayoutState.REFRESHING && pullY <= headLayout.getDistHeight()) {
//
//                }
                pullY = Math.max(0, pullY - 8);
            } else {
                pullY = Math.min(0, pullY + 8);
            }
            if (pullY != 0) {
                scrollBack();
            } else {
                changeState(RefreshLayoutState.INIT);
            }
            layoutChildren();
            invalidate();
        }
    };

    private void layoutChildren() {
        LayoutParams lp = (LayoutParams) bodyLayout.getLayoutParams();
        bodyLayout.layout(lp.leftMargin, (int) (pullY + lp.topMargin),
                getMeasuredWidth() - lp.rightMargin, (int) (pullY + getMeasuredHeight() - lp.bottomMargin));
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分,下拉大于0，上拉小于0
        if (type != RefreshLayoutType.ONLY_PULL_UP && headLayout.getDistHeight() > 0) {
            headLayout.layout(0, (int) (pullY - headLayout.getMeasuredHeight()), getMeasuredWidth(),
                    (int) (pullY));
        }
//        getCheckListener().relayout(contentView, contentParams, pullDownY, pullUpY);
        if (type != RefreshLayoutType.ONLY_PULL_DOWN && footLayout.getDistHeight() > 0) {
            footLayout.layout(0, (int) (pullY + getMeasuredHeight()), getMeasuredWidth(),
                    (int) (pullY + getMeasuredHeight() + footLayout.getMeasuredHeight()));
        }
    }

    private ViewStateCheckListener getCheckListener() {
        if (checkListener == null) {
            checkListener = new BaseViewStateCheckImpl();
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
            if (this.state == RefreshLayoutState.LOADING || this.state == RefreshLayoutState.TO_LOAD) {
                this.state = RefreshLayoutState.DONE;
                scrollBack();
            } else {
                this.state = RefreshLayoutState.DONE;
                scrollBack();
            }
        } else {
            this.state = state;
        }
    }
}
