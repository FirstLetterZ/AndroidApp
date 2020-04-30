package com.zpf.support.view.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.zpf.api.OnItemViewClickListener;
import com.zpf.support.util.LogUtil;
import com.zpf.tool.SafeClickListener;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZPF on 2018/9/27.
 */
public class BannerPagerView extends ViewPager {
    private AtomicInteger current = new AtomicInteger(1);//当前页码
    private long pauseTime; //按下的时间
    private volatile boolean isPaused = false;
    /*可设置的参数*/
    private int interval = 4000;//自动滚动间隔时间
    private int restartWait = 2000;//抬手后重新开始轮播的最小等待时间
    private int scrollTime = 1600;//完成每次滚动的耗时
    private Rect rect = new Rect();
    private BannerIndicator bannerIndicator;
    private AtomicInteger pagerSize = new AtomicInteger(0);
    private BannerViewCreator viewCreator;
    private PagerAdapter pagerAdapter;
    private float lastPositionOffset = 0;
    private boolean rebuildAllView = false;
    private boolean hasAttached = false;
    private boolean parentVisible = true;
    private boolean windowVisible = false;
    private boolean hasDraw = false;

    private boolean realVisible = false;
    private boolean autoScrollable = true;
    private OnItemViewClickListener itemViewClickListener;
    private View firstView;
    private View lastView;

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
                if (pagerSize.get() <= 1) {
                    return;
                }
                if (position > pagerSize.get() || (position == pagerSize.get() && positionOffset >= 0.99 && lastPositionOffset >= 0.9 && lastPositionOffset <= positionOffset)) {
                    if (current.get() == 1) {
                        return;
                    }
                    current.set(1);
                    setCurrentItem(current.get(), false);
                } else if (position == 0 && positionOffset <= 0.01 && lastPositionOffset <= 0.09 && lastPositionOffset >= positionOffset) {
                    if (current.get() == pagerSize.get()) {
                        return;
                    }
                    current.set(pagerSize.get());
                    setCurrentItem(current.get(), false);
                } else if (bannerIndicator != null) {
                    bannerIndicator.onScroll(position - 1, positionOffset);
                }
                lastPositionOffset = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                current.set(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    lastPositionOffset = 0;
                }
            }
        });
    }

    private Runnable circulationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecyclable()) {
                if (current.get() < pagerSize.get() + 1) {
                    current.getAndIncrement();
                    setCurrentItem(current.get(), true);
                } else {
                    //应该永远到不了这里
                    current.set(1);
                    setCurrentItem(current.get(), false);
                }
                postDelayed(circulationRunnable, interval);
            } else {
                removeCallbacks(circulationRunnable);
            }
        }
    };


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!hasDraw) {
            hasDraw = true;
            checkStateChanged();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return pagerSize.get() > 1 && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                restart();
                break;
            case MotionEvent.ACTION_DOWN:
                pause();
                return true;//返回false则无法监听MotionEvent.ACTION_UP
            default://正常不会是running状态
                pause();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void onParentVisibilityChanged(boolean visibility) {
        parentVisible = visibility;
        checkStateChanged();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        windowVisible = visibility == View.VISIBLE;
        checkStateChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hasAttached = false;
        checkStateChanged();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        hasAttached = true;
        checkStateChanged();
    }

    private boolean checkVisible() {
        if (windowVisible && hasAttached && parentVisible && hasDraw) {
            return getGlobalVisibleRect(rect);
        } else {
            return false;
        }
    }

    private boolean isRecyclable() {
        if (autoScrollable && pagerSize.get() > 1) {
            realVisible = checkVisible();
            return realVisible;
        } else {
            return false;
        }
    }

    private boolean checkStateChanged() {
        if (checkVisible() != realVisible) {
            realVisible = !realVisible;
            if (realVisible) {
                adjustPosition();
                restart();
            } else {
                pause();
            }
            return true;
        }
        return false;
    }

    private int getRealPosition() {
        if (current.get() == 0) {
            return pagerSize.get() - 1;
        } else if (current.get() == pagerSize.get() + 1) {
            return 0;
        } else {
            return current.addAndGet(-1);
        }
    }

    private class ViewPagerScroller extends Scroller {

        ViewPagerScroller(Context context) {
            super(context);
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
        pause();
        this.viewCreator = viewCreator;
        if (viewCreator == null) {
            resetSize(0, false);
        } else {
            resetSize(viewCreator.getSize(), false);
        }
    }

    public void resetSize(int newSize, boolean rebuild) {
        pause();
        if (bannerIndicator != null) {
            bannerIndicator.setSize(newSize);
        }
        rebuildAllView = rebuild || pagerSize.get() != newSize;
        pagerSize.set(newSize);
        if (newSize < 1) {
            firstView = null;
            lastView = null;
            pagerAdapter.notifyDataSetChanged();
        } else if (newSize == 1) {
            firstView = null;
            lastView = null;
            pagerAdapter.notifyDataSetChanged();
            setCurrentItem(0, false);
        } else {
            firstView = viewCreator.onCreateView(0);
            lastView = viewCreator.onCreateView(newSize + 1);
            pagerAdapter.notifyDataSetChanged();
            setCurrentItem(1, false);
            restart();
        }
    }

    private void adjustPosition() {
        if (isPaused && lastPositionOffset != 0) {
            lastPositionOffset = 0;
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
                final int rulePosition;
                View child = null;
                if (position == 0) {
                    child = firstView;
                    rulePosition = pagerSize.get() - 1;
                } else if (position == pagerSize.get() + 1) {
                    child = lastView;
                    rulePosition = 0;
                } else {
                    rulePosition = position - 1;
                }
                if (child == null) {
                    child = viewCreator.onCreateView(rulePosition);
                    if (position == 0) {
                        firstView = child;
                    } else if (position == pagerSize.get() + 1) {
                        lastView = child;
                    }
                }
                if (child.getParent() != null) {
                    ((ViewGroup) child.getParent()).removeView(child);
                }
                child.setOnClickListener(new SafeClickListener() {
                    @Override
                    public void click(View v) {
                        if (itemViewClickListener != null) {
                            itemViewClickListener.onItemViewClick(rulePosition, v);
                        }
                    }
                });
                child.setTag(position);
                viewCreator.onBindView(child, rulePosition);
                container.addView(child);
                return child;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                if (rebuildAllView) {
                    LogUtil.e("getItemPosition-->position=" + POSITION_NONE);
                    return POSITION_NONE;
                }
                int position = -1;
                try {
                    position = ((int) ((View) object).getTag());
                } catch (Exception e) {
                    //
                }
                if (position < 0) {
                    LogUtil.e("getItemPosition-->position=getItemPosition");
                    return super.getItemPosition(object);
                } else {
                    final int rulePosition;
                    if (position == 0) {
                        rulePosition = pagerSize.get() - 1;
                    } else if (position == pagerSize.get() + 1) {
                        rulePosition = 0;
                    } else {
                        rulePosition = position - 1;
                    }
                    viewCreator.onBindView((View) object, rulePosition);
                    LogUtil.e("getItemPosition-->position=" + position);
                    return position;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                if (pagerSize.get() > 1) {
                    return pagerSize.get() + 2;
                } else {
                    return pagerSize.get();
                }
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
        if (!isPaused) {
            return;
        }
        if (isRecyclable()) {
            //按住了多久
            long hold = System.currentTimeMillis() - pauseTime;
            long wait;
            if (interval - hold < restartWait) {
                wait = restartWait;
            } else {
                wait = interval - hold;
            }
            isPaused = false;
            postDelayed(circulationRunnable, wait);
        } else {
            pause();
        }
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            removeCallbacks(circulationRunnable);
            pauseTime = System.currentTimeMillis();
        }
    }

    public void setItemViewClickListener(OnItemViewClickListener itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
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
