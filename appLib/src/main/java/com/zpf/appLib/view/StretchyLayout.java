package com.zpf.appLib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zpf.appLib.view.def.DefaultCheckImpl;
import com.zpf.appLib.view.def.DefaultStretchyView;
import com.zpf.appLib.view.interfaces.ViewStateCheckListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZPF on 2017/10/26.
 * 彷ios回弹布局，上拉刷新，下拉加载
 */

public class StretchyLayout extends RelativeLayout {
    public static final int NO_STRETCHY = 0;//不可上下滑动
    public static final int BOTH_UP_DOWN = 1;//刷拉加载
    public static final int ONLY_PULL_DOWN = 2;//仅下拉
    public static final int ONLY_PULL_UP = 3;//仅上拉
    public static final int ONLY_STRETCHY = 4;//可上下拉但不刷新

    public static final int INIT = 0;  // 初始状态
    public static final int TO_REFRESH = 1; // 释放刷新
    public static final int REFRESHING = 2;  // 正在刷新
    public static final int TO_LOAD = 3;// 释放加载
    public static final int LOADING = 4;// 正在加载
    public static final int DONE = 5;// 操作完毕
    public static final int SUCCEED = 10; // 刷新成功
    public static final int FAIL = 11; // 刷新失败

    private int state = INIT;// 当前状态
    private int type = ONLY_STRETCHY;//当前类型
    private float lastY;
    private float mInterceptTouchDownX = -1;//判断滑动的主要方向
    private float mInterceptTouchDownY = -1;//判断滑动的主要方向
    private int mEvents;// 过滤多点触碰
    public float pullDownY = 0; //下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
    private float pullUpY = 0; //上拉的距离
    private float refreshDist;//刷新布局悬停高度
    private float loadmoreDist; //加载布局悬停高度
    private boolean isTouch = false;// 在刷新过程中滑动操作
    // 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
    private boolean canPullDown = true;
    private boolean canPullUp = true;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;
    private OnRefreshListener mListener;//监听
    public float MOVE_SPEED = 8; //回滚速度
    private MyTimer timer;
    private StretchyHeadOrFootView refreshLayout;//刷新布局
    private StretchyHeadOrFootView loadLayout;//加载布局
    private View contentView;
    private ViewStateCheckListener checkListener;
    private int loadDelayed = 500;
    private int refreshDelayed = 800;
    private boolean ifFinishInflate;

    public StretchyLayout(View contentView, StretchyHeadOrFootView refreshLayout,
                          StretchyHeadOrFootView loadLayout, int type) {
        super(contentView.getContext(), null, 0);
        this.contentView = contentView;
        this.refreshLayout = refreshLayout;
        this.loadLayout = loadLayout;
        this.type = type;
        timer = new MyTimer(updateHandler);
    }

    public StretchyLayout(Context context) {
        this(context, null, 0);
    }

    public StretchyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StretchyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithStyle(defStyleAttr);
        timer = new MyTimer(updateHandler);
    }

    public void initWithStyle(int defStyleAttr) {
        if (defStyleAttr == 0) {
            setCheckListener(new DefaultCheckImpl());
            setHeadFootLayout(new DefaultStretchyView(getContext()), new DefaultStretchyView(getContext()));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        ifFinishInflate = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (type == NO_STRETCHY) {
            super.onLayout(changed, l, t, r, b);
            return;
        }
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分,下拉大于0，上拉小于0
        if (refreshLayout != null) {
            refreshLayout.getContainerLayout().layout(0,
                    (int) (pullDownY + pullUpY) - refreshLayout.getContainerLayout().getMeasuredHeight(),
                    refreshLayout.getContainerLayout().getMeasuredWidth(), (int) (pullDownY + pullUpY));
        }
        if (checkListener != null) {
            checkListener.relayout(contentView, pullDownY, pullUpY);
        }
        if (loadLayout != null) {
            loadLayout.getContainerLayout().layout(0,
                    (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight(),
                    loadLayout.getContainerLayout().getMeasuredWidth(),
                    (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight()
                            + loadLayout.getContainerLayout().getMeasuredHeight());
        }
    }

    /**
     * 由父控件决定是否分发事件，防止事件冲突
     *
     * @see ViewGroup#dispatchTouchEvent(MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                mInterceptTouchDownX = ev.getRawX();
                mInterceptTouchDownY = ev.getRawY();
                timer.cancel();
                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 过滤多点触碰
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    if (mInterceptTouchDownX == -1) {
                        mInterceptTouchDownX = (int) ev.getRawX();
                    }
                    if (mInterceptTouchDownY == -1) {
                        mInterceptTouchDownY = (int) ev.getRawY();
                    }
                    int interceptTouchMoveDistanceY = (int) (ev.getRawY() - mInterceptTouchDownY);
                    if ((pullDownY > 0 || (canPullDown && state != LOADING && getCheckListener().checkPullDown(contentView)))
                            && (Math.abs(ev.getRawX() - mInterceptTouchDownX) < Math.abs(interceptTouchMoveDistanceY))) {
                        // 可以下拉，正在加载时不能下拉
                        // 对实际滑动距离做缩小，造成用力拉的感觉
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY <= 0) {
                            pullDownY = 0;
                            canPullDown = false;
                            canPullUp = true;
                            requestLayout();
                        }
                        if (pullDownY > 5 * refreshDist) {
                            pullDownY = 5 * refreshDist;
                        }
                        if (pullDownY > getMeasuredHeight()) {
                            pullDownY = getMeasuredHeight();
                        }
                        if (state == REFRESHING) {
                            // 正在刷新的时候触摸移动
                            isTouch = true;
                        }
                    } else if ((pullUpY < 0 || (canPullUp && state != REFRESHING && getCheckListener().checkPullUp(contentView)))
                            && (Math.abs(ev.getRawX() - mInterceptTouchDownX) < Math.abs(interceptTouchMoveDistanceY))) {
                        // 可以上拉，正在刷新时不能上拉
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY >= 0) {
                            pullUpY = 0;
                            canPullDown = true;
                            canPullUp = false;
                            requestLayout();
                        }
                        if (pullUpY < -5 * loadmoreDist) {
                            pullUpY = -5 * loadmoreDist;
                        }
                        if (pullUpY < -getMeasuredHeight()) {
                            pullUpY = -getMeasuredHeight();
                        }
                        if (state == LOADING) {
                            // 正在加载的时候触摸移动
                            isTouch = true;
                        }
                    } else {
                        releasePull();
                    }
                } else {
                    mEvents = 0;
                }
                lastY = ev.getY();
                // 根据下拉距离改变比例*
                radio = (float) (2 + 2 * Math.tan((Math.PI / 2)
                        * 2 * (Math.abs(pullDownY + pullUpY)) / getMeasuredHeight()));
                if (pullDownY > 0) {
                    requestLayout();
                    if (pullDownY <= refreshDist
                            && (state == TO_REFRESH || state == DONE)) {
                        // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                        changeState(INIT);
                    }
                    if (pullDownY >= refreshDist && state == INIT) {
                        // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                        changeState(TO_REFRESH);
                    }
                } else if (pullUpY < 0) {
                    requestLayout();
                    // 下面是判断上拉加载的，同上，注意pullUpY是负值
                    if (-pullUpY <= loadmoreDist
                            && (state == TO_LOAD || state == DONE)) {
                        changeState(INIT);
                    }
                    // 上拉操作
                    if (-pullUpY >= loadmoreDist && state == INIT) {
                        changeState(TO_LOAD);
                    }
                }
                // 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
                // Math.abs(pullUpY))就可以不对当前状态作区分了
                if ((pullDownY + Math.abs(pullUpY)) > 8) {
                    // 防止下拉过程中误触发长按事件和点击事件
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                mInterceptTouchDownX = -1;
                mInterceptTouchDownY = -1;
                // 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
                if (pullDownY > refreshDist || -pullUpY > loadmoreDist) {
                    isTouch = false;
                }
                if (state == TO_REFRESH && (type == ONLY_PULL_DOWN || type == BOTH_UP_DOWN)) {
                    changeState(REFRESHING);
                    // 刷新操作
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                } else if (state == TO_LOAD && (type == ONLY_PULL_UP || type == BOTH_UP_DOWN)) {
                    changeState(LOADING);
                    // 加载操作
                    if (mListener != null) {
                        mListener.onLoadMore();
                    }
                }
                hide();
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void initViews() {
        if (getChildCount() == 0 && contentView != null) {
            if (contentView.getParent() != null) {
                ((ViewGroup) contentView.getParent()).removeView(contentView);
            }
            addView(contentView);
        } else if (getChildCount() == 1 && contentView == null) {
            contentView = getChildAt(0);
        } else {
            this.type = NO_STRETCHY;
        }
        setType(type);
        if (refreshLayout != null) {
            addView(refreshLayout.getContainerLayout(), 0);
            refreshDist = refreshLayout.getDistHeight();
        }
        if (loadLayout != null) {
            addView(loadLayout.getContainerLayout());
            loadmoreDist = loadLayout.getDistHeight();
        }
    }

    //每隔5m秒执行一次updateHandler，完成回滚
    private void hide() {
        timer.schedule(5);
    }

    //上拉或下拉后重回状态，不限制上拉或下拉
    private void releasePull() {
        canPullDown = (type == ONLY_PULL_DOWN || type == ONLY_STRETCHY || type == BOTH_UP_DOWN);
        canPullUp = (type == ONLY_PULL_UP || type == ONLY_STRETCHY || type == BOTH_UP_DOWN);
    }

    private void changeState(int state) {
        if (!(this.state == TO_LOAD || this.state == LOADING)
                && !(state == TO_LOAD || state == LOADING)) {
            refreshLayout.changeState(state);
        }
        if (!(this.state == TO_REFRESH || this.state == REFRESHING)
                && !(state == TO_REFRESH || state == REFRESHING)) {
            loadLayout.changeState(state);
        }
        if (state == SUCCEED || state == FAIL) {
            if (this.state == LOADING || this.state == TO_LOAD) {
                this.state = DONE;
                timer.cancel();
                pullDownY = 0;
                pullUpY = -loadmoreDist;
                requestLayout();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                }, loadDelayed);
            } else {
                this.state = DONE;
                timer.cancel();
                pullDownY = refreshDist;
                pullUpY = 0;
                requestLayout();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                }, refreshDelayed);
            }

        } else {
            this.state = state;
        }
    }

    public void setType(int type) {
        this.type = type;
        if (refreshLayout != null) {
            refreshLayout.setType(type);
        }
        if (loadLayout != null) {
            loadLayout.setType(type);
        }
    }

    public void setHeadFootLayout(StretchyHeadOrFootView headLayout, StretchyHeadOrFootView footLayout) {
        if (headLayout != null) {
            this.refreshLayout = headLayout;
        }
        if (footLayout != null) {
            this.loadLayout = footLayout;
        }
        setType(type);
    }

    public void setDelayedTime(int refreshDelayed, int loadDelayed) {
        if (refreshDelayed > 200) {
            this.refreshDelayed = refreshDelayed;
        }
        if (loadDelayed >= 0) {
            this.loadDelayed = loadDelayed;
        }
    }

    public void setRefreshResult(boolean success) {
        changeState(success ? SUCCEED : FAIL);
    }

    public void doRefresh() {
        if (state != INIT || (type != ONLY_PULL_DOWN && type != BOTH_UP_DOWN)) {
            return;
        }
        if (ifFinishInflate) {
            pullDownY = refreshDist;
            pullUpY = 0;
            isTouch = false;
            requestLayout();
            changeState(REFRESHING);
            if (mListener != null) {
                mListener.onRefresh();
            }
        } else {
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doRefresh();
                }
            }, 200);
        }
    }

    public void setCheckListener(ViewStateCheckListener checkListener) {
        this.checkListener = checkListener;
    }

    public ViewStateCheckListener getCheckListener() {
        return checkListener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        //刷新操作
        void onRefresh();

        //加载操作
        void onLoadMore();
    }

    //执行updateHandler
    private class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    @SuppressLint("HandlerLeak")
    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (state == DONE) {
                state = INIT;
                if (pullDownY >= refreshDist / 2) {
                    pullDownY = refreshDist;
                    pullUpY = 0;
                } else if (pullUpY <= -loadDelayed / 2) {
                    pullDownY = 0;
                    pullUpY = -loadDelayed;
                }
                requestLayout();
                return;
            }
            // 回弹速度随下拉距离moveDeltaY增大而增大
            MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
                    / getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
            if (pullDownY > 0) {
                pullDownY -= MOVE_SPEED;
            } else if (pullUpY < 0) {
                pullUpY += MOVE_SPEED;
            }
            if (!isTouch) {
                // 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
                if (state == REFRESHING && pullDownY <= refreshDist) {
                    pullDownY = refreshDist;
                    timer.cancel();
                } else if (state == LOADING && -pullUpY <= loadmoreDist) {
                    pullUpY = -loadmoreDist;
                    timer.cancel();
                }
            }
            if (pullDownY < 0) {
                // 已完成回弹
                pullDownY = 0;
                // 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != REFRESHING && state != LOADING) {
                    changeState(INIT);
                }
                timer.cancel();
            }
            if (pullUpY > 0) {
                // 已完成回弹
                pullUpY = 0;
                // 隐藏上拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != REFRESHING && state != LOADING) {
                    changeState(INIT);
                }
                timer.cancel();
            }
            requestLayout();
            // 没有拖拉或者回弹完成
            if (pullDownY + Math.abs(pullUpY) == 0) {
                timer.cancel();
            }
        }

    };

}
