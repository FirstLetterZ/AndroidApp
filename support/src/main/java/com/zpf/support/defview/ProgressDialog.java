package com.zpf.support.defview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zpf.support.base.BaseDialog;
import com.zpf.support.base.R;

/**
 * Created by ZPF on 2018/6/14.
 */
public class ProgressDialog extends BaseDialog {

    protected TextView textView;

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
    }

    @Override
    protected void initWindow(@NonNull Window window) {
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.getAttributes().gravity = Gravity.CENTER;
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void setText(String content) {
        if (textView != null) {
            textView.setText(content);
        }
    }

}
