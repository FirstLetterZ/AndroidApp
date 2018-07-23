package com.zpf.baselib.ui.refresh;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZPF on 2017/10/26.
 * 彷ios回弹布局，上拉刷新，下拉加载
 */
public abstract class AbsRefreshLayout extends RelativeLayout {
    private int state = RefreshLayoutState.INIT;// 当前状态
    private int type = RefreshLayoutType.ONLY_STRETCHY;//当前类型
    private float lastY;
    private float mInterceptTouchDownX = -1;//判断滑动的主要方向
    private float mInterceptTouchDownY = -1;//判断滑动的主要方向
    private int mEvents;// 过滤多点触碰
    private float pullDownY = 0; //下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
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
    private float MOVE_SPEED = 8; //回滚速度
    private MyTimer timer;
    private AbsHeadFootLayout refreshLayout;//刷新布局
    private AbsHeadFootLayout loadLayout;//加载布局
    private View contentView;
    private ViewStateCheckListener checkListener;
    protected int loadDelayed = 600;//完成后停顿时间
    protected int refreshDelayed = 800;//完成后停顿时间
    private boolean hasAddView;
    private boolean hasLayoutView;

    public AbsRefreshLayout(View contentView, AbsHeadFootLayout refreshLayout,
                            AbsHeadFootLayout loadLayout, int type) {
        super(contentView.getContext(), null, 0);
        this.contentView = contentView;
        this.refreshLayout = refreshLayout;
        this.loadLayout = loadLayout;
        this.type = type;
        timer = new MyTimer(updateHandler);
        initViews();
    }

    public AbsRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public AbsRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        timer = new MyTimer(updateHandler);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    //绘制布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!hasLayoutView) {
            hasLayoutView = true;
        }
        if (type == RefreshLayoutType.NO_STRETCHY) {
            super.onLayout(changed, l, t, r, b);
            return;
        }
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分,下拉大于0，上拉小于0
        if (refreshLayout != null) {
            refreshLayout.getContainerLayout().layout(0,
                    (int) (pullDownY + pullUpY) - refreshLayout.getContainerLayout().getMeasuredHeight(),
                    refreshLayout.getContainerLayout().getMeasuredWidth(), (int) (pullDownY + pullUpY));
        }
        getCheckListener().relayout(contentView, pullDownY, pullUpY);
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
                    if ((pullDownY > 0 || (canPullDown && state != RefreshLayoutState.LOADING
                            && getCheckListener().checkPullDown(contentView)))
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
                        if (state == RefreshLayoutState.REFRESHING) {
                            // 正在刷新的时候触摸移动
                            isTouch = true;
                        }
                    } else if ((pullUpY < 0 || (canPullUp && state != RefreshLayoutState.REFRESHING && getCheckListener().checkPullUp(contentView)))
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
                        if (state == RefreshLayoutState.LOADING) {
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
                            && (state == RefreshLayoutState.TO_REFRESH || state == RefreshLayoutState.DONE)) {
                        // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                        changeState(RefreshLayoutState.INIT);
                    }
                    if (pullDownY >= refreshDist && state == RefreshLayoutState.INIT) {
                        // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                        changeState(RefreshLayoutState.TO_REFRESH);
                    }
                } else if (pullUpY < 0) {
                    requestLayout();
                    // 下面是判断上拉加载的，同上，注意pullUpY是负值
                    if (-pullUpY <= loadmoreDist
                            && (state == RefreshLayoutState.TO_LOAD || state == RefreshLayoutState.DONE)) {
                        changeState(RefreshLayoutState.INIT);
                    }
                    // 上拉操作
                    if (-pullUpY >= loadmoreDist && state == RefreshLayoutState.INIT) {
                        changeState(RefreshLayoutState.TO_LOAD);
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
                if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
                // 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
                {
                    isTouch = false;
                }
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
            this.type = RefreshLayoutType.NO_STRETCHY;
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
        hasAddView = true;
    }

    //每隔5m秒执行一次updateHandler，完成回滚
    private void hide() {
        timer.schedule(5);
    }

    //上拉或下拉后重回状态，不限制上拉或下拉
    private void releasePull() {
        canPullDown = true;
        canPullUp = true;
    }

    private void changeState(@RefreshLayoutState int state) {
        if (!(this.state == RefreshLayoutState.TO_LOAD || this.state == RefreshLayoutState.LOADING)
                && !(state == RefreshLayoutState.TO_LOAD || state == RefreshLayoutState.LOADING)) {
            if (refreshLayout != null) {
                refreshLayout.changeState(state);
            }
        }
        if (!(this.state == RefreshLayoutState.TO_REFRESH || this.state == RefreshLayoutState.REFRESHING)
                && !(state == RefreshLayoutState.TO_REFRESH || state == RefreshLayoutState.REFRESHING)) {
            if (loadLayout != null) {
                loadLayout.changeState(state);
            }
        }
        if (state == RefreshLayoutState.SUCCEED || state == RefreshLayoutState.FAIL) {
            if (this.state == RefreshLayoutState.LOADING || this.state == RefreshLayoutState.TO_LOAD) {
                this.state = RefreshLayoutState.DONE;
                timer.cancel();
                pullDownY = 0;
                pullUpY = -loadmoreDist;
                requestLayout();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                }, loadDelayed);
            } else {
                this.state = RefreshLayoutState.DONE;
                timer.cancel();
                pullDownY = refreshDist;
                pullUpY = 0;
                requestLayout();
                postDelayed(new Runnable() {
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

    private ViewStateCheckListener getCheckListener() {
        if (checkListener == null) {
            checkListener = getDefaultViewStateCheckListener();
        }
        return checkListener;
    }

    public void setType(@RefreshLayoutType int type) {
        this.type = type;
        if (refreshLayout == null && type != RefreshLayoutType.NO_STRETCHY) {
            refreshLayout = getDefaultHeadLayout(getContext());
        }
        if (loadLayout == null && type != RefreshLayoutType.NO_STRETCHY) {
            loadLayout = getDefaultFootLayout(getContext());
        }
        if (refreshLayout != null) {
            refreshLayout.setType(type);
        }
        if (loadLayout != null) {
            loadLayout.setType(type);
        }
    }

    public void setHeadFootLayout(AbsHeadFootLayout headLayout, AbsHeadFootLayout footLayout) {
        if (hasAddView) {
            if (refreshLayout != null) {
                removeView(refreshLayout.getContainerLayout());
            }
            if (loadLayout != null) {
                removeView(loadLayout.getContainerLayout());
            }
            if (headLayout != null) {
                addView(headLayout, 0);
            }
            if (footLayout != null) {
                addView(footLayout);
            }
        }
        this.refreshLayout = headLayout;
        this.loadLayout = footLayout;
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
        if (this.state == RefreshLayoutState.INIT || this.state == RefreshLayoutState.DONE) {
            return;
        }
        changeState(success ? RefreshLayoutState.SUCCEED : RefreshLayoutState.FAIL);
    }

    public void doRefresh() {
        if (state != RefreshLayoutState.INIT) {
            return;
        }
        if (hasLayoutView) {
            if (contentView != null) {
                if (contentView instanceof PackedLayoutInterface) {
                    ((PackedLayoutInterface) contentView).getCurrentChild().scrollTo(0, 0);
                } else {
                    contentView.scrollTo(0, 0);
                }
            }
            pullDownY = refreshDist;
            pullUpY = 0;
            isTouch = false;
            requestLayout();
            changeState(RefreshLayoutState.REFRESHING);
            if (mListener != null) {
                mListener.onRefresh();
            }
        } else {
            postDelayed(new Runnable() {
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

    Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (state == RefreshLayoutState.DONE) {
                state = RefreshLayoutState.INIT;
                if (pullDownY >= refreshDist / 2) {
                    pullDownY = refreshDist;
                    pullUpY = 0;
                } else if (pullUpY <= -loadDelayed / 2) {
                    pullDownY = 0;
                    pullUpY = -loadDelayed;
                }
                requestLayout();
                return true;
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
                if (state == RefreshLayoutState.REFRESHING && pullDownY <= refreshDist) {
                    pullDownY = refreshDist;
                    timer.cancel();
                } else if (state == RefreshLayoutState.LOADING && -pullUpY <= loadmoreDist) {
                    pullUpY = -loadmoreDist;
                    timer.cancel();
                }
            }
            if (pullDownY < 0) {
                // 已完成回弹
                pullDownY = 0;
                // 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != RefreshLayoutState.REFRESHING && state != RefreshLayoutState.LOADING) {
                    changeState(RefreshLayoutState.INIT);
                }
                timer.cancel();
            }
            if (pullUpY > 0) {
                // 已完成回弹
                pullUpY = 0;
                // 隐藏上拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                if (state != RefreshLayoutState.REFRESHING && state != RefreshLayoutState.LOADING) {
                    changeState(RefreshLayoutState.INIT);
                }
                timer.cancel();
            }
            requestLayout();
            // 没有拖拉或者回弹完成
            if (pullDownY + Math.abs(pullUpY) == 0) {
                timer.cancel();
            }
            return true;
        }
    });

    @NonNull
    public abstract AbsHeadFootLayout getDefaultHeadLayout(Context context);

    @NonNull
    public abstract AbsHeadFootLayout getDefaultFootLayout(Context context);

    //添加其他布局的时候需要复写
    protected ViewStateCheckListener getDefaultViewStateCheckListener() {
        return new BaseViewStateCheckImpl();
    }
}
