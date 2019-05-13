package com.zpf.support.single;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpf.api.IBackPressInterceptor;
import com.zpf.support.R;

/**
 * Created by ZPF on 2019/5/13.
 */
public class LoadingLayout extends LinearLayout implements IBackPressInterceptor {
    protected TextView textView;
    private boolean cancelable = false;
    static OnClickListener emptyClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public LoadingLayout(Context context) {
        this(context, null, 0);
    }

    public LoadingLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_loding, this, true);
        textView = view.findViewById(R.id.tvLoadingPrompt_lib);
    }

    public void setCancelable(boolean cancelable) {
        if (cancelable) {
            setOnClickListener(null);
        } else {
            setOnClickListener(emptyClick);
        }
    }

    public void setText(String content) {
        if (textView != null) {
            textView.setText(content);
        }
    }

    @Override
    public boolean onInterceptBackPress() {
        return getVisibility() == VISIBLE && !cancelable;
    }
}
