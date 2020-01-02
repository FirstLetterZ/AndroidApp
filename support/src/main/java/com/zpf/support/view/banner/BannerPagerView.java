package com.zpf.support.view.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.zpf.tool.TimeTaskUtil;

import java.lang.reflect.Field;

/**
 * Created by ZPF on 2018/9/27.
 */
public class BannerPagerView extends ViewPager {
    private int current = 1;//当前页码
    private float x;//用于判断是否为水平滑动
    private float y;//用于判断是否为竖直滑动
    private boolean isMove;//滑动不触发点击事件
    private long pauseTime; //按下的时间
    private long hold;//按住了多久
    private long lastClick;//上次触发点击事件的事件
    private boolean scrollable = true;
    private volatile boolean isPaused = false;
    /*可设置的参数*/
    private int interval = 4000;//自动滚动间隔时间
    private int restartWait = 2000;//抬手后重新开始轮播的最小等待时间
    private int scrollTime = 1600;//完成每次滚动的耗时

    private BannerIndicator bannerIndicator;
    private int pagerSize;
    private BannerViewCreator viewCreator;
    private View[] pagerArray;
    private PagerAdapter pagerAdapter;
    private float lastPositionOffset = 0;
    private boolean rebuildAllView = false;
    private boolean hasAttached = false;
    private boolean windowVisible = false;
    private boolean autoScrollable = true;

    public BannerPagerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ViewPagerScroller scroller = new ViewPagerScroller(context);
        scroller.initScroller(this);
        initAdapter();
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (pagerSize <= 1) {
                    return;
                }
                if (position > pagerSize || (position == pagerSize && positionOffset >= 0.99 && lastPositionOffset >= 0.9 && lastPositionOffset <= positionOffset)) {
                    current = 1;
                    setCurrentItem(1, false);
                } else if (position == 0 && positionOffset <= 0.01 && lastPositionOffset <= 0.09 && lastPositionOffset >= positionOffset) {
                    current = pagerSize;
                    setCurrentItem(pagerSize, false);
                } else if (bannerIndicator != null) {
                    bannerIndicator.onScroll(position - 1, positionOffset);
                }
                lastPositionOffset = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    lastPositionOffset = 0;
                }
            }
        });
    }

    private TimeTaskUtil timeTaskUtil = new TimeTaskUtil() {
        @Override
        protected void doInMainThread() {
            if (current < pagerSize + 1) {
                current++;
                setCurrentItem(current, true);
            } else {
                //应该永远到不了这里
                current = 1;
                setCurrentItem(current, false);
            }
        }

        @Override
        protected void doInChildThread() {

        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scrollable && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                restart();
                if (viewCreator != null && pagerAdapter != null && pagerAdapter.getCount() > 0 && hold < 800 && !isMove) {//按住小于0.8秒，点击事件不为空，不是滑动状态，则出发点击
                    if (System.currentTimeMillis() - lastClick > 200) {//防抖
                        lastClick = System.currentTimeMillis();
                        viewCreator.onItemClick(getRealPosition());
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                pause();
                x = event.getRawX();
                y = event.getRawY();
                isMove = false;
                return true;//返回false则无法监听MotionEvent.ACTION_UP
            case MotionEvent.ACTION_CANCEL:
                restart();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getRawX() - x) > 8 || Math.abs(event.getRawY() - y) > 8) {//水平或竖直方向位移绝对值超过8像素视为滑动
                    isMove = true;
                }
            default://正常不会是running状态
                timeTaskUtil.stopPlay();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        windowVisible = visibility == View.VISIBLE;
        if (windowVisible) {
            adjustPosition();
            restart();
        } else {
            pause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hasAttached = false;
        pause();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        hasAttached = true;
        adjustPosition();
        restart();
    }

    private int getRealPosition() {
        if (current == 0) {
            return pagerSize - 1;
        } else if (current == pagerSize + 1) {
            return 0;
        } else {
            return current - 1;
        }
    }

    private class ViewPagerScroller extends Scroller {

        ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, scrollTime);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, scrollTime);
        }

        void initScroller(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //初始化
    public void init(BannerViewCreator viewCreator) {
        timeTaskUtil.stopPlay();
        isPaused = true;
        this.viewCreator = viewCreator;
        if (viewCreator == null) {
            resetSize(0, false);
        } else {
            resetSize(viewCreator.getSize(), false);
        }
    }

    public void resetSize(int newSize, boolean rebuild) {
        timeTaskUtil.stopPlay();
        isPaused = true;
        if (bannerIndicator != null) {
            bannerIndicator.setSize(newSize);
        }
        rebuildAllView = rebuild || pagerSize != newSize;
        pagerSize = newSize;
        if (newSize < 1) {
            scrollable = false;
            pagerArray = null;
            pagerAdapter.notifyDataSetChanged();
        } else if (newSize == 1) {
            pagerArray = new View[1];
            scrollable = false;
            pagerAdapter.notifyDataSetChanged();
            setCurrentItem(0, false);
        } else {
            pagerArray = new View[pagerSize + 2];
            scrollable = true;
            pagerAdapter.notifyDataSetChanged();
            setCurrentItem(1, false);
            restart();
        }
    }

    private void adjustPosition() {
        if (isPaused && lastPositionOffset != 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentItem(getRealPosition(), false);
                }
            }, 10);
        }
    }

    private void initAdapter() {
        pagerAdapter = new PagerAdapter() {
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, final int position) {
                View v = pagerArray[position];
                if (v == null) {
                    int rulePosition;
                    if (position == 0) {
                        rulePosition = pagerSize - 1;
                    } else if (position == pagerSize + 1) {
                        rulePosition = 0;
                    } else {
                        rulePosition = position - 1;
                    }
                    v = viewCreator.createView(rulePosition);
                    pagerArray[position] = v;
                }
                if (v.getParent() != null) {
                    ((ViewGroup) v.getParent()).removeView(v);
                }
                container.addView(v);
                return v;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                if (rebuildAllView) {
                    return POSITION_NONE;
                }
                return super.getItemPosition(object);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return pagerArray == null ? 0 : pagerArray.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        };
        setAdapter(pagerAdapter);
    }

    public void notifyDataSetChanged(boolean rebuildAllView) {
        if (viewCreator != null) {
            resetSize(viewCreator.getSize(), rebuildAllView);
        }
    }

    public void setBannerIndicator(BannerIndicator bannerIndicator) {
        this.bannerIndicator = bannerIndicator;
    }

    public void restart() {
        if (pagerAdapter.getCount() > 1 && autoScrollable && isPaused && hasAttached && windowVisible) {
            hold = System.currentTimeMillis() - pauseTime;
            long wait;
            if (interval - hold < restartWait) {
                wait = restartWait;
            } else {
                wait = interval - hold;
            }
            timeTaskUtil.startPlay(wait, interval);
            isPaused = false;
        }
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            timeTaskUtil.stopPlay();
            pauseTime = System.currentTimeMillis();
        }
    }

    public int getScrollTime() {
        return scrollTime;
    }

    public void setScrollTime(int scrollTime) {
        this.scrollTime = scrollTime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getRestartWait() {
        return restartWait;
    }

    public void setRestartWait(int restartWait) {
        this.restartWait = restartWait;
    }

    public int getCount() {
        return pagerAdapter.getCount();
    }

    public boolean isAutoScrollable() {
        return autoScrollable;
    }

    public void setAutoScrollable(boolean autoScrollable) {
        this.autoScrollable = autoScrollable;
        if (autoScrollable) {
            restart();
        } else {
            pause();
        }
    }
}
