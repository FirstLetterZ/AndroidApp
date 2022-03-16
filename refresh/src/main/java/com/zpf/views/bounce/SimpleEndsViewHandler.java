package com.zpf.views.bounce;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2018/6/15.
 */
public class SimpleEndsViewHandler extends FrameLayout implements IBothEndsViewHandler {
    private final TextView mTextView;
    private final ProgressBar mProgressBar;
    private String initText;
    private boolean onlyText;
    private int state = -1;
    private int type = -1;
    private boolean isFootLayout = false;

    public SimpleEndsViewHandler(@NonNull Context context) {
        this(context, null, 0);
    }

    public SimpleEndsViewHandler(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleEndsViewHandler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float d = context.getResources().getDisplayMetrics().density;
        mProgressBar = new ProgressBar(context);
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.parseColor("#333333"));
        addView(mTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams flp = new LayoutParams((int) (16 * d), (int) (16 * d));
        flp.setMarginStart((int) (context.getResources().getDisplayMetrics().widthPixels / 4 - 16 * d));
        flp.gravity = Gravity.CENTER_VERTICAL;
        addView(mProgressBar, flp);
    }

    @Override
    public void onStateChange(int sate) {
        if (type != BounceLayoutType.ONLY_STRETCHY && type != BounceLayoutType.NO_STRETCHY && this.state != sate) {
            this.state = sate;
            switch (sate) {
                case BounceLayoutState.INIT:
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText(initText);
                    break;
                case BounceLayoutState.TO_REFRESH:
                    if (!isFootLayout && (type == BounceLayoutType.BOTH_UP_DOWN || type == BounceLayoutType.ONLY_PULL_DOWN)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("释放刷新页面");
                    }
                    break;
                case BounceLayoutState.REFRESHING:
                    if (!isFootLayout && (type == BounceLayoutType.BOTH_UP_DOWN || type == BounceLayoutType.ONLY_PULL_DOWN)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("正在刷新...");
                    }
                    break;
                case BounceLayoutState.TO_LOAD:
                    if (isFootLayout && (type == BounceLayoutType.BOTH_UP_DOWN || type == BounceLayoutType.ONLY_PULL_UP)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("释放加载更多");
                    }
                    break;
                case BounceLayoutState.LOADING:
                    if (isFootLayout && (type == BounceLayoutType.BOTH_UP_DOWN || type == BounceLayoutType.ONLY_PULL_UP)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("正在加载...");
                    }
                    break;
                case BounceLayoutState.SUCCEED:
                    if (isFootLayout && (type == BounceLayoutType.ONLY_PULL_UP || type == BounceLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("加载成功");
                    } else if (!isFootLayout && (type == BounceLayoutType.ONLY_PULL_DOWN || type == BounceLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("刷新成功");
                    }
                    break;
                case BounceLayoutState.FAIL:
                    if (isFootLayout && (type == BounceLayoutType.ONLY_PULL_UP || type == BounceLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("加载失败");
                    } else if (!isFootLayout && (type == BounceLayoutType.ONLY_PULL_DOWN || type == BounceLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("刷新失败");
                    }
                    break;
            }
        }
    }

    @Override
    public void onTypeChange(int type) {
        if (this.type != type) {
            this.type = type;
            this.state = BounceLayoutState.INIT;
            switch (type) {
                default:
                case BounceLayoutType.NO_STRETCHY:
                    onlyText = true;
                    break;
                case BounceLayoutType.BOTH_UP_DOWN:
                    onlyText = false;
                    if (isFootLayout) {
                        initText = "上拉加载更多";
                    } else {
                        initText = "下拉刷新页面";
                    }
                    break;
                case BounceLayoutType.ONLY_PULL_DOWN:
                    if (isFootLayout) {
                        onlyText = true;
                        initText = "-------- 我是有底线的 --------";
                    } else {
                        onlyText = false;
                        initText = "下拉刷新";
                    }
                    break;
                case BounceLayoutType.ONLY_PULL_UP:
                    if (isFootLayout) {
                        onlyText = false;
                        initText = "上拉加载";
                    } else {
                        onlyText = true;
                        initText = "-------- 已经到头了 --------";
                    }
                    break;
                case BounceLayoutType.ONLY_STRETCHY:
                    onlyText = true;
                    if (isFootLayout) {
                        initText = "-------- 我是有底线的 --------";
                    } else {
                        initText = "-------- 已经到头了 --------";
                    }
                    break;
            }
            mTextView.setText(initText);
        }
    }

    @Override
    public int getDistHeight() {
        return getMeasuredHeight();
    }

    @Override
    public View onCreateView(@NonNull ViewGroup parent, boolean isFootLayout) {
        this.isFootLayout = isFootLayout;
        return this;
    }

}
