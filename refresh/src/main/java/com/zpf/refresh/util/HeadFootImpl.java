package com.zpf.refresh.util;

import android.graphics.Color;

import androidx.annotation.NonNull;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by ZPF on 2018/6/15.
 */
public class HeadFootImpl implements HeadFootInterface {
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private String initText;
    private boolean onlyText;
    private int state = -1;
    private int type = -1;
    private boolean isFootLayout = false;
    private int mDistHeight;
    private View mLayout;

    @Override
    public void onStateChange(int sate) {
        if (type != RefreshLayoutType.ONLY_STRETCHY && type != RefreshLayoutType.NO_STRETCHY && this.state != sate) {
            this.state = sate;
            switch (sate) {
                case RefreshLayoutState.DONE:
                case RefreshLayoutState.INIT:
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText(initText);
                    break;
                case RefreshLayoutState.TO_REFRESH:
                    if (!isFootLayout && (type == RefreshLayoutType.BOTH_UP_DOWN || type == RefreshLayoutType.ONLY_PULL_DOWN)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("释放刷新页面");
                    }
                    break;
                case RefreshLayoutState.REFRESHING:
                    if (!isFootLayout && (type == RefreshLayoutType.BOTH_UP_DOWN || type == RefreshLayoutType.ONLY_PULL_DOWN)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("正在刷新...");
                    }
                    break;
                case RefreshLayoutState.TO_LOAD:
                    if (isFootLayout && (type == RefreshLayoutType.BOTH_UP_DOWN || type == RefreshLayoutType.ONLY_PULL_UP)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("释放加载更多");
                    }
                    break;
                case RefreshLayoutState.LOADING:
                    if (isFootLayout && (type == RefreshLayoutType.BOTH_UP_DOWN || type == RefreshLayoutType.ONLY_PULL_UP)) {
                        mProgressBar.setVisibility(onlyText ? View.GONE : View.VISIBLE);
                        mTextView.setText("正在加载...");
                    }
                    break;
                case RefreshLayoutState.SUCCEED:
                    if (isFootLayout && (type == RefreshLayoutType.ONLY_PULL_UP || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("加载成功");
                    } else if (!isFootLayout && (type == RefreshLayoutType.ONLY_PULL_DOWN || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("刷新成功");
                    }
                    break;
                case RefreshLayoutState.FAIL:
                    if (isFootLayout && (type == RefreshLayoutType.ONLY_PULL_UP || type == RefreshLayoutType.BOTH_UP_DOWN)) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setText("加载失败");
                    } else if (!isFootLayout && (type == RefreshLayoutType.ONLY_PULL_DOWN || type == RefreshLayoutType.BOTH_UP_DOWN)) {
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
            this.state = RefreshLayoutState.INIT;
            switch (type) {
                default:
                case RefreshLayoutType.NO_STRETCHY:
                    onlyText = true;
                    break;
                case RefreshLayoutType.BOTH_UP_DOWN:
                    onlyText = false;
                    if (isFootLayout) {
                        initText = "上拉加载更多";
                    } else {
                        initText = "下拉刷新页面";
                    }
                    break;
                case RefreshLayoutType.ONLY_PULL_DOWN:
                    if (isFootLayout) {
                        onlyText = true;
                        initText = "-------- 我是有底线的 --------";
                    } else {
                        onlyText = false;
                        initText = "下拉刷新";
                    }
                    break;
                case RefreshLayoutType.ONLY_PULL_UP:
                    if (isFootLayout) {
                        onlyText = false;
                        initText = "上拉加载";
                    } else {
                        onlyText = true;
                        initText = "-------- 已经到头了 --------";
                    }
                    break;
                case RefreshLayoutType.ONLY_STRETCHY:
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
        return mDistHeight;
    }

    @Override
    public View onCreateView(@NonNull ViewGroup parent, boolean isFootLayout) {
        this.isFootLayout = isFootLayout;
        if (mLayout == null) {
            float d = parent.getResources().getDisplayMetrics().density;
            mDistHeight = (int) (44 * d);
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);
            mProgressBar = new ProgressBar(parent.getContext());
            mTextView = new TextView(parent.getContext());
            mTextView.setPadding((int) (8 * d), 0, (int) (8 * d), 0);
            mTextView.setTextColor(Color.parseColor("#333333"));
            layout.addView(mProgressBar, new ViewGroup.LayoutParams((int) (16 * d), (int) (16 * d)));
            layout.addView(mTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mLayout = layout;
        }
        return mLayout;
    }

}
