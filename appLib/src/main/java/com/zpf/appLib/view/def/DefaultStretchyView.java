package com.zpf.appLib.view.def;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpf.appLib.view.StretchyHeadOrFootView;
import com.zpf.appLib.view.StretchyLayout;

/**
 * Created by ZPF on 2017/10/26.
 */

public class DefaultStretchyView extends StretchyHeadOrFootView {
    private TextView mTextView;
    private RelativeLayout mRefreshLayout;
    private ImageView mArrowView;
    private String initText;

    public DefaultStretchyView(Context context) {
        super(context);
    }

    public DefaultStretchyView(Context context, boolean isLoadLayout) {
        super(context, isLoadLayout);
    }

    @Override
    public void changeState(int sate) {
        if (type == StretchyLayout.ONLY_STRETCHY || type == StretchyLayout.NO_STRETCHY) {
            return;
        } else if (this.state != sate) {
            this.state = sate;
            switch (sate) {
                case StretchyLayout.DONE:
                case StretchyLayout.INIT:
                    mArrowView.setVisibility(View.VISIBLE);
                    this.state = sate;
                    mTextView.setText(initText);
                    break;
                case StretchyLayout.TO_REFRESH:
                    if (!isLoadLayout && (type == StretchyLayout.BOTH_UP_DOWN || type == StretchyLayout.ONLY_PULL_DOWN)) {
                        this.state = sate;
                        mTextView.setText("释放刷新页面");
                    }
                    break;
                case StretchyLayout.REFRESHING:
                    if (!isLoadLayout && (type == StretchyLayout.BOTH_UP_DOWN || type == StretchyLayout.ONLY_PULL_DOWN)) {
                        this.state = sate;
                        mArrowView.setVisibility(View.GONE);
                        mTextView.setText("正在刷新...");
                    }
                    break;
                case StretchyLayout.TO_LOAD:
                    if (isLoadLayout && (type == StretchyLayout.BOTH_UP_DOWN || type == StretchyLayout.ONLY_PULL_UP)) {
                        this.state = sate;
                        mTextView.setText("释放加载更多");
                    }
                    break;
                case StretchyLayout.LOADING:
                    if (isLoadLayout && (type == StretchyLayout.BOTH_UP_DOWN || type == StretchyLayout.ONLY_PULL_UP)) {
                        mArrowView.setVisibility(View.GONE);
                        mTextView.setText("正在加载...");
                    }
                    break;
                case StretchyLayout.SUCCEED:
                    if (isLoadLayout && (type == StretchyLayout.ONLY_PULL_UP || type == StretchyLayout.BOTH_UP_DOWN)) {
                        mTextView.setText("加载成功");
                    } else if (!isLoadLayout && (type == StretchyLayout.ONLY_PULL_DOWN || type == StretchyLayout.BOTH_UP_DOWN)) {
                        mTextView.setText("刷新成功");
                    }
                    break;
                case StretchyLayout.FAIL:
                    if (isLoadLayout && (type == StretchyLayout.ONLY_PULL_UP || type == StretchyLayout.BOTH_UP_DOWN)) {
                        mTextView.setText("加载失败");
                    } else if (!isLoadLayout && (type == StretchyLayout.ONLY_PULL_DOWN || type == StretchyLayout.BOTH_UP_DOWN)) {
                        mTextView.setText("刷新失败");
                    }
                    break;
            }
        }
    }

    @Override
    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            this.state = StretchyLayout.INIT;
            switch (type) {
                default:
                case StretchyLayout.NO_STRETCHY:
                    break;
                case StretchyLayout.BOTH_UP_DOWN:
                    if (isLoadLayout) {
                        initText = "上拉加载更多";
                    } else {
                        initText = "下拉刷新页面";
                    }
                    break;
                case StretchyLayout.ONLY_PULL_DOWN:
                    if (isLoadLayout) {
                        initText = "--------------------------------我是有底线的--------------------------------";
                    } else {
                        initText = "下拉刷新";
                    }
                    break;
                case StretchyLayout.ONLY_PULL_UP:
                    if (isLoadLayout) {
                        initText = "上拉加载";
                    } else {
                        initText = "--------------------------------已经到头了--------------------------------";
                    }
                    break;
                case StretchyLayout.ONLY_STRETCHY:
                    mArrowView.setVisibility(View.GONE);
                    if (isLoadLayout) {
                        initText = "--------------------------------我是有底线的--------------------------------";
                    } else {
                        initText = "--------------------------------已经到头了--------------------------------";
                    }
                    break;
            }
            mTextView.setText(initText);
        }
    }

    @Override
    public int getDistHeight() {
        return (int) (40 * density);
    }

    @Override
    public View getRefreshLayout() {
        if (mRefreshLayout == null) {
            initRefreshLayout();
        }
        return mRefreshLayout;
    }

    private void initRefreshLayout() {
        mRefreshLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams refreshViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) (40 * density));
        mRefreshLayout.setLayoutParams(refreshViewParams);

        mTextView = new TextView(getContext());
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mArrowView = new ImageView(getContext());
        RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        arrowParams.setMargins((int) (40 * density), 0, 0, 0);
        arrowParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        mRefreshLayout.addView(mArrowView, arrowParams);
        mRefreshLayout.addView(mTextView, textViewParams);
    }

}
