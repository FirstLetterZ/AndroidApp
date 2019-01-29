package com.zpf.refresh.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.zpf.api.PackedLayoutInterface;
import com.zpf.refresh.util.BaseViewStateCheckImpl;
import com.zpf.refresh.util.HeadFootImpl;
import com.zpf.refresh.util.HeadFootInterface;
import com.zpf.refresh.util.OnRefreshListener;
import com.zpf.refresh.util.RefreshLayoutState;
import com.zpf.refresh.util.RefreshLayoutType;
import com.zpf.refresh.util.ViewStateCheckListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZPF on 2017/10/26.
 * 彷ios回弹布局，上拉刷新，下拉加载
 */
public class RefreshLayout extends FrameLayout {
    private int state = RefreshLayoutState.INIT;// 当前状态
    private int type = RefreshLayoutType.ONLY_STRETCHY;//当前类型
    private float lastY;
    private float mInterceptTouchDownX = -1;//判断滑动的主要方向
    private float mInterceptTouchDownY = -1;//判断滑动的主要方向
    private int mEvents;// 过滤多点触碰
    private float pullDownY = 0; //下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
    private float pullUpY = 0; //上拉的距离
    private boolean isTouch = false;// 在刷新过程中滑动操作
    // 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
    private boolean canPullDown = true;
    private boolean canPullUp = true;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;
    private OnRefreshListener mListener;//监听
    private float MOVE_SPEED = 8; //回滚速度
    private MyTimer timer;
    private HeadFootLayout refreshLayout;//刷新布局
    private HeadFootLayout loadLayout;//加载布局
    private View contentView;
    private ViewStateCheckListener checkListener;
    protected int loadDelayed = 600;//完成后停顿时间
    protected int refreshDelayed = 800;//完成后停顿时间
    private boolean hasWindowFocus = false;
    private FrameLayout.LayoutParams contentParams;

    public RefreshLayout(View contentView) {
        this(contentView.getContext(), null, 0);
        this.contentView = contentView;
    }

    public RefreshLayout(Context context) {
        this(context, null, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshLayout = new HeadFootLayout(context);
        refreshLayout.setHeadFootInterface(new HeadFootImpl());
        loadLayout = new HeadFootLayout(context, true);
        loadLayout.setHeadFootInterface(new HeadFootImpl());
        timer = new MyTimer(updateHandler);
        super.setPadding(0, 0, 0, 0);
    }

    /**
     * 暂不支持padding
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
//        super.setPadding(left, top, right, bottom);
    }

    /**
     * 暂不支持padding
     */
    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
//        super.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (contentView != null) {
            removeAllViews();
            addView(refreshLayout);
            addView(loadLayout);
            addView(contentView);
            contentParams = (LayoutParams) contentView.getLayoutParams();
        } else if (childCount == 1) {
            contentView = getChildAt(0);
            contentParams = (LayoutParams) contentView.getLayoutParams();
            addView(refreshLayout);
            addView(loadLayout);
        } else {
            throw new IllegalStateException("只能有一个控件");
        }
    }

    //绘制布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (type == RefreshLayoutType.NO_STRETCHY) {
            super.onLayout(changed, l, t, r, b);
            return;
        }
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分,下拉大于0，上拉小于0
        if (type != RefreshLayoutType.ONLY_PULL_UP && refreshLayout.getDistHeight() > 0) {
            refreshLayout.layout(0, (int) (pullDownY + pullUpY) - refreshLayout.getMeasuredHeight(),
                    refreshLayout.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        }
        if (contentParams == null) {
            contentParams = (LayoutParams) contentView.getLayoutParams();
        }
        getCheckListener().relayout(contentView, contentParams, pullDownY, pullUpY);
        if (type != RefreshLayoutType.ONLY_PULL_DOWN && loadLayout.getDistHeight() > 0) {
            loadLayout.layout(0, (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight()
                            + contentParams.topMargin + contentParams.bottomMargin,
                    loadLayout.getMeasuredWidth(), (int) (pullDownY + pullUpY)
                            + contentView.getMeasuredHeight() + loadLayout.getMeasuredHeight()
                            + contentParams.topMargin + contentParams.bottomMargin);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        this.hasWindowFocus = hasWindowFocus;
    }

    /**
     * 由父控件决定是否分发事件，防止事件冲突
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
                    boolean isHorizontalScrolling = Math.abs(ev.getRawX() - mInterceptTouchDownX) > Math.abs(interceptTouchMoveDistanceY);
                    if (!isHorizontalScrolling && (pullDownY > 0 || (interceptTouchMoveDistanceY >= 0 && checkPullDown()))) {
                        // 可以下拉，正在加载时不能下拉
                        // 对实际滑动距离做缩小，造成用力拉的感觉
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY <= 0) {
                            pullDownY = 0;
                            canPullDown = false;
                            canPullUp = true;
                            requestLayout();
                        }
                        if (pullDownY > 5 * refreshLayout.getDistHeight()) {
                            pullDownY = 5 * refreshLayout.getDistHeight();
                        }
                        if (pullDownY > getMeasuredHeight()) {
                            pullDownY = getMeasuredHeight();
                        }
                        if (state == RefreshLayoutState.REFRESHING) {
                            // 正在刷新的时候触摸移动
                            isTouch = true;
                        }
                    } else if (!isHorizontalScrolling && (pullUpY < 0 || (interceptTouchMoveDistanceY <= 0 && checkPullUp()))) {
                        // 可以上拉，正在刷新时不能上拉
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY >= 0) {
                            pullUpY = 0;
                            canPullDown = true;
                            canPullUp = false;
                            requestLayout();
                        }
                        if (pullUpY < -5 * loadLayout.getDistHeight()) {
                            pullUpY = -5 * loadLayout.getDistHeight();
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
                    if (pullDownY <= refreshLayout.getDistHeight()
                            && (state == RefreshLayoutState.TO_REFRESH || state == RefreshLayoutState.DONE)) {
                        // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                        changeState(RefreshLayoutState.INIT);
                    }
                    if (pullDownY >= refreshLayout.getDistHeight() && state == RefreshLayoutState.INIT) {
                        // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                        changeState(RefreshLayoutState.TO_REFRESH);
                    }
                } else if (pullUpY < 0) {
                    requestLayout();
                    // 下面是判断上拉加载的，同上，注意pullUpY是负值
                    if (-pullUpY <= loadLayout.getDistHeight()
                            && (state == RefreshLayoutState.TO_LOAD || state == RefreshLayoutState.DONE)) {
                        changeState(RefreshLayoutState.INIT);
                    }
                    // 上拉操作
                    if (-pullUpY >= loadLayout.getDistHeight() && state == RefreshLayoutState.INIT) {
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
                // 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
                if (pullDownY > refreshLayout.getDistHeight() || -pullUpY > loadLayout.getDistHeight()) {
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
            refreshLayout.changeState(state);
        }
        if (!(this.state == RefreshLayoutState.TO_REFRESH || this.state == RefreshLayoutState.REFRESHING)
                && !(state == RefreshLayoutState.TO_REFRESH || state == RefreshLayoutState.REFRESHING)) {
            loadLayout.changeState(state);
        }
        if (state == RefreshLayoutState.SUCCEED || state == RefreshLayoutState.FAIL) {
            if (this.state == RefreshLayoutState.LOADING || this.state == RefreshLayoutState.TO_LOAD) {
                this.state = RefreshLayoutState.DONE;
                timer.cancel();
                pullDownY = 0;
                pullUpY = -loadLayout.getDistHeight();
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
                pullDownY = refreshLayout.getDistHeight();
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

    public void setHeadInterface(HeadFootInterface headInterface) {
        refreshLayout.setHeadFootInterface(headInterface);
    }

    public void setFootInterface(HeadFootInterface footInterface) {
        loadLayout.setHeadFootInterface(footInterface);
    }

    public boolean checkPullDown() {
        return canPullDown && state != RefreshLayoutState.LOADING && isContentToTop();
    }

    public boolean checkPullUp() {
        return canPullUp && state != RefreshLayoutState.REFRESHING && isContentToBottom();
    }

    public boolean isContentToBottom() {
        return getCheckListener().checkPullUp(contentView);
    }

    public boolean isContentToTop() {
        return getCheckListener().checkPullDown(contentView);
    }

    public void setType(@RefreshLayoutType int type) {
        this.type = type;
        refreshLayout.setType(type);
        loadLayout.setType(type);
    }

    public int getType() {
        return type;
    }

    //设置刷新和加载布局停留时间
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

    //模拟刷新
    public void doRefresh() {
        if (state != RefreshLayoutState.INIT) {
            return;
        }
        tryRefresh(3);
    }

    private void tryRefresh(final int time) {
        if (hasWindowFocus) {
            scrollContentTop(contentView);
            pullDownY = refreshLayout.getDistHeight();
            pullUpY = 0;
            isTouch = false;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                    changeState(RefreshLayoutState.REFRESHING);
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            }, 200);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryRefresh(time - 1);
                }
            }, 400);
        }
    }

    private void scrollContentTop(View view) {
        if (view != null) {
            if (view instanceof PackedLayoutInterface) {
                scrollContentTop(((PackedLayoutInterface) view).getCurrentChild());
            } else {
                view.scrollTo(0, 0);
            }
        }
    }

    public HeadFootLayout getHeadLayout() {
        return refreshLayout;
    }

    public HeadFootLayout getFootLayout() {
        return loadLayout;
    }

    public void setCheckListener(ViewStateCheckListener checkListener) {
        this.checkListener = checkListener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public boolean isInitState() {
        return pullDownY + Math.abs(pullUpY) == 0;
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
                if (pullDownY >= refreshLayout.getDistHeight() / 2) {
                    pullDownY = refreshLayout.getDistHeight();
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
                if (state == RefreshLayoutState.REFRESHING && pullDownY <= refreshLayout.getDistHeight()) {
                    pullDownY = refreshLayout.getDistHeight();
                    timer.cancel();
                } else if (state == RefreshLayoutState.LOADING && -pullUpY <= loadLayout.getDistHeight()) {
                    pullUpY = -loadLayout.getDistHeight();
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

    //添加其他布局的时候需要复写
    protected ViewStateCheckListener getDefaultViewStateCheckListener() {
        return new BaseViewStateCheckImpl();
    }
}
