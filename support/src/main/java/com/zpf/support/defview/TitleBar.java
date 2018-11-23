package com.zpf.support.defview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zpf.api.TitleBarInterface;

/**
 * Created by ZPF on 2018/6/14.
 */
public class TitleBar extends RelativeLayout implements TitleBarInterface {
    private IconTextView tvLeft;
    private IconTextView ivLeft;
    private LinearLayout leftLayout;
    private LinearLayout titleLayout;
    private IconTextView title;
    private IconTextView subtitle;
    private LinearLayout rightLayout;
    private IconTextView ivRight;
    private IconTextView tvRight;

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (44 * metrics.density)));
        RelativeLayout rootLayout = new RelativeLayout(getContext());
        rootLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        leftLayout = new LinearLayout(context);
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        ivLeft = new IconTextView(context);
        ivLeft.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ivLeft.setPadding((int) (10 * metrics.density), 0, 0, 0);
        ivLeft.setMinWidth((int) (30 * metrics.density));
        ivLeft.setGravity(Gravity.CENTER);
        ivLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                }
            }
        });

        tvLeft = new IconTextView(context);
        tvLeft.setTextColor(Color.WHITE);
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvLeft.setPadding((int) (10 * metrics.density), 0, 0, 0);

        leftLayout.addView(ivLeft);
        leftLayout.addView(tvLeft);

        titleLayout = new LinearLayout(context);
        int titleLayoutWidth = (int) (metrics.widthPixels - 160 * metrics.density);
        LayoutParams titleLayoutParams = new LayoutParams(titleLayoutWidth,
                LayoutParams.MATCH_PARENT);
        titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setLayoutParams(titleLayoutParams);
        titleLayout.setGravity(Gravity.CENTER);

        title = new IconTextView(context);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setTextColor(Color.WHITE);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setSingleLine();
        title.setMaxLines(1);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        subtitle = new IconTextView(context);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        subtitle.setEllipsize(TextUtils.TruncateAt.END);
        subtitle.setSingleLine();
        subtitle.setMaxLines(1);
        subtitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        subtitle.setVisibility(GONE);

        titleLayout.addView(title);
        titleLayout.addView(subtitle);

        rightLayout = new LinearLayout(context);
        LayoutParams rightLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightLayout.setLayoutParams(rightLayoutParams);
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);

        ivRight = new IconTextView(context);
        ivRight.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ivRight.setMinWidth((int) (30 * metrics.density));
        ivRight.setGravity(Gravity.CENTER);
        ivRight.setPadding(0, 0, (int) (10 * metrics.density), 0);

        tvRight = new IconTextView(context);
        tvRight.setTextColor(Color.WHITE);
        tvRight.setGravity(Gravity.CENTER);
        tvRight.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvRight.setPadding(0, 0, (int) (10 * metrics.density), 0);

        rightLayout.addView(tvRight);
        rightLayout.addView(ivRight);

        rootLayout.addView(leftLayout);
        rootLayout.addView(titleLayout);
        rootLayout.addView(rightLayout);
        addView(rootLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ivLeft.checkViewShow();
        tvLeft.checkViewShow();
        ivRight.checkViewShow();
        tvRight.checkViewShow();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public IconTextView getLeftImage() {
        return ivLeft;
    }

    @Override
    public IconTextView getLeftText() {
        return tvLeft;
    }

    @Override
    public ViewGroup getLeftLayout() {
        return leftLayout;
    }

    @Override
    public IconTextView getRightImage() {
        return ivRight;
    }

    @Override
    public IconTextView getRightText() {
        return tvRight;
    }

    @Override
    public ViewGroup getRightLayout() {
        return rightLayout;
    }

    @Override
    public IconTextView getTitle() {
        return title;
    }

    @Override
    public IconTextView getSubTitle() {
        return subtitle;
    }

    @Override
    public ViewGroup getTitleLayout() {
        return titleLayout;
    }

    @Override
    public ViewGroup getLayout() {
        return this;
    }
}