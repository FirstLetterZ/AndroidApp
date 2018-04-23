package com.zpf.appLib.view.def;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zpf.appLib.util.SafeClickListener;
import com.zpf.appLib.view.TitleBarLayout;

/**
 * Created by ZPF on 2018/4/16.
 */

public class DefaultTitleBar extends TitleBarLayout {

    private TextView tvLeftOne;
    private TextView tvLeftTwo;
    private ImageView ivLeft;
    private LinearLayout leftLayout;
    private LinearLayout titleLayout;
    private TextView title;
    private TextView subtitle;
    private LinearLayout rightLayout;
    private ImageView ivRight;
    private TextView tvRightOne;
    private TextView tvRightTwo;

    public DefaultTitleBar(Context context) {
        this(context, null, 0);
    }

    public DefaultTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (44 * metrics.density)));

        leftLayout = new LinearLayout(context);
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        leftLayout.setPadding((int) (8 * metrics.density), 0, 0, 0);

        ivLeft = new ImageView(context);
        ivLeft.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ivLeft.setPadding((int) (8 * metrics.density), 0, 0, 0);

        tvLeftOne = new TextView(context);
        tvLeftOne.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvLeftOne.setPadding((int) (8 * metrics.density), 0, 0, 0);

        tvLeftTwo = new TextView(context);
        tvLeftTwo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvLeftTwo.setPadding((int) (8 * metrics.density), 0, 0, 0);

        leftLayout.addView(ivLeft);
        leftLayout.addView(tvLeftOne);
        leftLayout.addView(tvLeftTwo);

        titleLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams titleLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLayoutParams.addRule(CENTER_IN_PARENT);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setLayoutParams(titleLayoutParams);
        titleLayout.setGravity(Gravity.CENTER);

        title = new TextView(context);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        subtitle = new TextView(context);
        subtitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        titleLayout.addView(title);
        titleLayout.addView(subtitle);

        rightLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams rightLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rightLayoutParams.addRule(ALIGN_RIGHT);
        rightLayout.setLayoutParams(rightLayoutParams);
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);
        rightLayout.setPadding(0, 0, 0, (int) (8 * metrics.density));

        ivRight = new ImageView(context);
        ivRight.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ivRight.setPadding(0, 0, 0, (int) (8 * metrics.density));

        tvRightOne = new TextView(context);
        tvRightOne.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvRightOne.setPadding(0, 0, 0, (int) (8 * metrics.density));

        tvRightTwo = new TextView(context);
        tvRightTwo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvRightTwo.setPadding(0, 0, 0, (int) (8 * metrics.density));

        rightLayout.addView(ivRight);
        rightLayout.addView(tvRightOne);
        rightLayout.addView(tvRightTwo);
        initViewStyle();
    }

    private void initViewStyle() {
        setBackgroundColor(Color.BLUE);
        leftLayout.setOnClickListener(new SafeClickListener() {
            @Override
            public void click(View v) {
                try {
                    ((Activity) v.getContext()).finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ivLeft.setVisibility(VISIBLE);
        tvLeftOne.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvLeftOne.setTextColor(Color.WHITE);
        tvLeftOne.setVisibility(GONE);
        tvLeftTwo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvLeftTwo.setTextColor(Color.WHITE);
        tvLeftTwo.setVisibility(GONE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        title.setTextColor(Color.WHITE);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setVisibility(GONE);
        ivRight.setVisibility(GONE);
        tvRightOne.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvRightOne.setTextColor(Color.WHITE);
        tvRightOne.setVisibility(GONE);
        tvRightTwo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvRightTwo.setTextColor(Color.WHITE);
        tvRightTwo.setVisibility(GONE);
    }

    @Override
    public TextView getFirstLeftText() {
        return tvLeftOne;
    }

    @Override
    public TextView getSecondLeftText() {
        return tvLeftTwo;
    }

    @Override
    public View getLeftLayout() {
        return leftLayout;
    }

    @Override
    public ImageView getLeftImage() {
        return ivLeft;
    }

    @Override
    public TextView getFirstRightText() {
        return tvRightOne;
    }

    @Override
    public TextView getSecondRightText() {
        return tvRightTwo;
    }

    @Override
    public ImageView getRightImage() {
        return ivRight;
    }

    @Override
    public View getRightLayout() {
        return rightLayout;
    }

    @Override
    public TextView getTitle() {
        return title;
    }

    @Override
    public TextView getSubTitle() {
        return subtitle;
    }

    @Override
    public View getTitleLayout() {
        return titleLayout;
    }
}
