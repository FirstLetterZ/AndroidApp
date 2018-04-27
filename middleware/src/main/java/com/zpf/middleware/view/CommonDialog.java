package com.zpf.middleware.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zpf.appLib.base.BaseDialog;
import com.zpf.appLib.util.SafeClickListener;
import com.zpf.middleware.R;

/**
 * Created by ZPF on 2018/4/27.
 */

public class CommonDialog extends BaseDialog {
    private ImageView dialogIcon;
    private TextView dialogTitle;
    private TextView dialogMessage;
    private EditText dialogInput;
    private View line;
    private Button dialogCancel;
    private Button dialogConfirm;

    public CommonDialog(@NonNull Context context) {
        super(context);
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.dialog_common);
        dialogIcon = $(R.id.iv_icon);
        dialogTitle = $(R.id.tv_title);
        dialogInput = $(R.id.et_input);
        dialogMessage = $(R.id.tv_msg);
        dialogCancel = $(R.id.btn_cancel);
        dialogConfirm = $(R.id.btn_confirm);
        line = $(R.id.view_line);
        initVisible();
        dialogCancel.setOnClickListener(new SafeClickListener() {
            @Override
            public void click(View v) {
                dismiss();
            }
        });
    }

    public void initVisible() {
        dialogIcon.setVisibility(View.GONE);
        dialogTitle.setVisibility(View.VISIBLE);
        dialogInput.setVisibility(View.GONE);
        dialogMessage.setVisibility(View.VISIBLE);
        dialogCancel.setVisibility(View.VISIBLE);
        dialogConfirm.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
    }

    public ImageView getDialogIcon() {
        return dialogIcon;
    }

    public TextView getDialogTitle() {
        return dialogTitle;
    }

    public TextView getDialogMessage() {
        return dialogMessage;
    }

    public EditText getDialogInput() {
        return dialogInput;
    }

    public View getSplitLine() {
        return line;
    }

    public Button getDialogCancel() {
        return dialogCancel;
    }

    public Button getDialogConfirm() {
        return dialogConfirm;
    }
}
