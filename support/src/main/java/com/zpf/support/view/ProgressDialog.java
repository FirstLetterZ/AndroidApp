package com.zpf.support.view;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zpf.support.R;
import com.zpf.tool.expand.view.CustomDialog;

/**
 * 默认的等待弹窗
 * Created by ZPF on 2018/6/14.
 */
public class ProgressDialog extends CustomDialog {

    protected TextView textView;
    private long minCancelTime = 3000;//时间内不能被取消
    private long showTime = 0;

    public ProgressDialog(@NonNull Context context) {
        this(context, R.style.customDialog);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.progress_loding);
        textView = findViewById(R.id.tvLoadingPrompt_lib);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initWindow(@NonNull Window window) {
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setDimAmount(0.16f);
        window.getAttributes().gravity = Gravity.CENTER;
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showTime = System.currentTimeMillis();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - showTime >= minCancelTime) {
            super.onBackPressed();
        }
    }

    public void setText(String content) {
        if (textView != null) {
            textView.setText(content);
            if (TextUtils.isEmpty(content)) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setMinCancelTime(long minCancelTime) {
        this.minCancelTime = minCancelTime;
    }
}
