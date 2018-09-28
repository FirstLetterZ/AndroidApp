package com.zpf.support.view.banner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.zpf.support.base.R;

/**
 * Created by ZPF on 2018/9/27.
 */
public class StretchableIndicator extends RelativeLayout implements BannerIndicator {
    private LinearLayout dotsLayout;//计数点的布局
    private View indicator;//前面指示点
    private LayoutParams indicatorParams;//指示点的布局参数
    private Drawable dotBack;//后面指示点图形
    private Drawable dotFront;//前面指示点图形
    private int dotSize;//指示点个数
    private int dotSpace;//指示点间距
    private int dotHeight;//指示点高度
    private int dotWidth;//指示点宽度
    private int defDotWidth;//
    private int indicatorDx;//指示点水平位移
    private int indicatorWidth;//指示点当前宽度

    private boolean scrollToBorder = false;//边界判断

    public StretchableIndicator(Context context) {
        this(context, null, 0);
    }

    public StretchableIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StretchableIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defDotWidth = (int) (8 * context.getResources().getDisplayMetrics().density);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StretchableIndicator);
        if (typedArray != null) {
            initValue(context, typedArray);
        }
        dotsLayout = new LinearLayout(context);  //前方指示点
        LayoutParams dotsParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, dotHeight);
        dotsParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        this.addView(dotsLayout, dotsParams);
        indicator = new View(context);  //前方指示点
        indicator.setBackground(dotFront);
        indicatorParams = new LayoutParams(dotWidth, dotHeight);
        indicatorParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        this.addView(indicator, indicatorParams);
        initDots();
    }

    private void initValue(Context context, TypedArray typedArray) {
        int dotFrontId = 0, dotBackId = 0;
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.StretchableIndicator_dotHeight) {
                dotHeight = typedArray.getDimensionPixelSize(attr, -1);
            } else if (attr == R.styleable.StretchableIndicator_dotWidth) {
                dotWidth = typedArray.getDimensionPixelSize(attr, -1);
            } else if (attr == R.styleable.StretchableIndicator_dotSpace) {
                dotSpace = typedArray.getDimensionPixelSize(attr, -1);
            } else if (attr == R.styleable.StretchableIndicator_dotBack) {
                dotBackId = typedArray.getResourceId(attr, 0);
            } else if (attr == R.styleable.StretchableIndicator_dotFront) {
                dotFrontId = typedArray.getResourceId(attr, EditorInfo.TYPE_NULL);
            }
        }
        typedArray.recycle();
        if (dotHeight <= 0) {
            dotHeight = defDotWidth;
        }
        if (dotWidth <= 0) {
            dotWidth = defDotWidth;
        }
        if (dotSpace <= 0) {
            dotSpace = defDotWidth;
        }
        Resources resources = context.getResources();
        if (dotBackId > 0) {
            try {
                dotBack = resources.getDrawable(dotBackId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }
        if (dotFrontId > 0) {
            try {
                dotFront = resources.getDrawable(dotFrontId);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }
        if (dotBack == null) {
            GradientDrawable back = new GradientDrawable();
            back.setCornerRadius((dotHeight + dotWidth) * 0.5f);
            back.setColor(Color.parseColor("#999999"));
            dotBack = back;
        }
        if (dotFront == null) {
            GradientDrawable front = new GradientDrawable();
            front.setCornerRadius((dotHeight + dotWidth) * 0.5f);
            front.setColor(Color.WHITE);
            dotFront = front;
        }
    }

    @Override
    public void onScroll(int position, float positionOffset) {
        if (dotSize <= 1) {
            return;
        }
        //通过改变指示小圆点的宽度和水平位来达到滑动指示动画效果
        if (positionOffset < 0.0001f) {
            scrollToBorder = false;
            indicatorDx = dotWidth * position + dotSpace * (position + 1);
            indicatorParams.setMargins(indicatorDx, 0, 0, 0);
            indicatorWidth = dotWidth;
        } else if (positionOffset < 0.4f) {//拉伸段
            if (position == -1 && scrollToBorder) {
                position = dotSize - 1;
            }
            indicatorWidth = (int) (dotWidth + dotSpace * positionOffset * 2);
            indicatorDx = dotWidth * position + dotSpace * (position + 1);
        } else if (positionOffset < 0.6f) {//平移段
            if (position != dotSize - 1 && position != -1) {
                scrollToBorder = false;
                indicatorWidth = (int) (dotWidth + dotSpace * 0.8f);
                indicatorDx = (int) (dotWidth * position + dotSpace * (position + 1) + (dotWidth + 0.2 * dotSpace) * (positionOffset - 0.4) / 0.2);
            } else {
                //已滚动到边界，需要对拉伸段和收缩段做特殊处理
                scrollToBorder = true;
            }
        } else {//收缩短
            if (position == dotSize - 1 && scrollToBorder) {
                position = -1;
            }
            indicatorWidth = (int) (dotWidth + 0.8 * dotSpace - 0.8 * dotSpace * (positionOffset - 0.6) / 0.4);
            indicatorDx = dotWidth * (position + 1) + dotSpace * (position + 2) + dotWidth - indicatorWidth;
        }
        indicatorParams.setMargins(indicatorDx, 0, 0, 0);
        indicatorParams.width = indicatorWidth;
        indicator.setLayoutParams(indicatorParams);
    }

    @Override
    public void setSize(int dotSize) {
        this.dotSize = dotSize;
        initDots();
    }

    private void initDots() {
        if (dotsLayout.getChildCount() > 0) {
            dotsLayout.removeAllViews();
        }
        if (dotSize <= 1) {
            return;
        }
        //添加计数小圆点
        for (int i = 0; i < dotSize; i++) {
            Space space = new Space(getContext());
            LinearLayout.LayoutParams lp01 = new LinearLayout.LayoutParams(dotSpace, LinearLayout.LayoutParams.WRAP_CONTENT);
            space.setLayoutParams(lp01);
            dotsLayout.addView(space);
            View dots = new View(getContext());
            LinearLayout.LayoutParams lp02 = new LinearLayout.LayoutParams(dotWidth, dotHeight);
            dots.setLayoutParams(lp02);
            dots.setBackground(dotBack);
            dotsLayout.addView(dots);
        }
        Space space = new Space(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dotSpace, LinearLayout.LayoutParams.WRAP_CONTENT);
        space.setLayoutParams(lp);
        dotsLayout.addView(space);
    }

}
