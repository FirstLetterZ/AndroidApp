package com.example.aplugin;


import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;

import com.zpf.api.ILayoutId;
import com.zpf.support.base.ViewProcessor;
import com.zpf.support.view.CommonDialog;
//import com.zpf.tool.config.AppContext;
//import com.zpf.tool.permission.PermissionChecker;
import com.zpf.tool.toast.ToastUtil;
import com.zpf.tool.toast.ToastWindow;


@ILayoutId(R.layout.activity_main)
public class TestSecondLayout extends ViewProcessor {
    //    TextView textView = (TextView) $(R.id.tv_msg);
    Button button = bind(R.id.btn_test);
    int i = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (PermissionChecker.checkDrawOverlays(getCurrentActivity())) {
//            ToastUtil.setToaster(new ToastWindow(AppContext.get()));
//        }
        button.setText(R.string.test_text01);
        CommonDialog dialog = new CommonDialog(getContext());
        dialog.getTitle().setVisibility(View.VISIBLE);
        dialog.getIcon().setVisibility(View.VISIBLE);
        dialog.getMessage().setVisibility(View.VISIBLE);
        dialog.getCancel().setVisibility(View.VISIBLE);
        dialog.getConfirm().setVisibility(View.VISIBLE);
        dialog.getConfirm().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                test("12345");
            }
        });
        dialog.getViewLine().setVisibility(View.VISIBLE);
        show(dialog);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_test) {
            String text;
            switch (i) {
                case 0:
                    text = getContext().getResources().getString(R.string.test_text02);
                    break;
                case 1:
                    text = getContext().getResources().getString(R.string.test_text03);
                    break;
                case 2:
                    text = "index == 2";
                    break;
                default:
                    i = 0;
                    text = getContext().getResources().getString(R.string.test_text01);
                    break;
            }
            i++;
            button.setText(text);
            test(text);
        }
    }

    private void test(CharSequence text) {
        ToastUtil.toast(text);
    }
}
