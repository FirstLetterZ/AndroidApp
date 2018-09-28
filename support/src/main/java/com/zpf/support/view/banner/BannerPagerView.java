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

import com.zpf.support.generalUtil.TimeTaskUtil;

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
    /*可设置的参数*/
    private int interval = 4000;//自动滚动间隔时间
    private int restartWait = 2000;//抬手后重新开始轮播的最小等待时间
    private int scrollTime = 2000;//完成每次滚动的耗时

    private BannerIndicator bannerIndicator;
    private int pagerSize;
    private BannerViewCreator viewCreator;
    private View[] pagerArray;

    public BannerPagerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ViewPagerScroller scroller = new ViewPagerScroller(context);
        scroller.initScroller(this);
        setAdapter(new PagerAdapter() {
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
        });
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (pagerSize <= 1) {
                    return;
                }
                if (positionOffset == 0) {
                    current = position;
                    if (position == 0) {
                        setCurrentItem(pagerSize, false);
                    } else if (position == pagerSize + 1) {
                        setCurrentItem(1, false);
                    } else if (bannerIndicator != null) {
                        bannerIndicator.onScroll(position - 1, positionOffset);
                    }
                } else if (bannerIndicator != null) {
                    bannerIndicator.onScroll(position - 1, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                current = 0;
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
                if (viewCreator != null && hold < 800 && !isMove) {//按住小于0.8秒，点击事件不为空，不是滑动状态，则出发点击
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

    private int getRealPosition() {
        if (current == 0) {
            return pagerSize;
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
        this.viewCreator = viewCreator;
        if (viewCreator == null) {
            resetSize(0);
        } else {
            resetSize(viewCreator.getSize());
        }
    }

    public void resetSize(int newSize) {
        timeTaskUtil.stopPlay();
        if (bannerIndicator != null) {
            bannerIndicator.setSize(newSize);
        }
        pagerSize = newSize;
        if (newSize < 1) {
            scrollable = false;
            pagerArray = null;
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        } else if (newSize == 1) {
            pagerArray = new View[1];
            scrollable = false;
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
            setCurrentItem(0, false);
        } else {
            pagerArray = new View[pagerSize + 2];
            scrollable = true;
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
            setCurrentItem(1, false);
            timeTaskUtil.startPlay(interval, interval);
        }
    }

    public void setBannerIndicator(BannerIndicator bannerIndicator) {
        this.bannerIndicator = bannerIndicator;
    }

    public void restart() {
        hold = System.currentTimeMillis() - pauseTime;
        long wait;
        if (interval - hold < restartWait) {
            wait = restartWait;
        } else {
            wait = interval - hold;
        }
        timeTaskUtil.startPlay(wait);
    }

    public void pause() {
        timeTaskUtil.stopPlay();
        pauseTime = System.currentTimeMillis();
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
}
