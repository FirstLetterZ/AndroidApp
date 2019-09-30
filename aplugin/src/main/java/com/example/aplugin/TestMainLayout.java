package com.example.aplugin;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpf.api.ILayoutId;
import com.zpf.support.base.ViewProcessor;
import com.zpf.tool.SafeClickListener;

@ILayoutId(R.layout.activity_main)
public class TestMainLayout extends ViewProcessor {
//    TextView textView = (TextView) $(R.id.tv_msg);
    Button button = (Button) $(R.id.btn_test);
    int i = 0;
    private View.OnClickListener btnClick = new SafeClickListener() {

        @Override
        public void click(View v) {
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
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button.setText(R.string.test_text01);
        button.setOnClickListener(btnClick);
    }

    //    @Override
//    protected View getLayoutView(Context context) {
//        LinearLayout layout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setGravity(Gravity.CENTER);
//
//        int padding = (int) (16 * getContext().getResources().getDisplayMetrics().density);
//        textView = new TextView(context);
//        textView.setText("plugin loaded successfully");
//        textView.setPadding(padding, padding, padding, padding);
//        layout.addView(textView);
//
//        button = new Button(context);
//        button.setText(R.string.test_text01);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String text;
//                switch (i) {
//                    case 0:
//                        text = getContext().getResources().getString(R.string.test_text02);
//                        break;
//                    case 1:
//                        text = getContext().getResources().getString(R.string.test_text03);
//                        break;
//                    case 2:
//                        text = "index == 2";
//                        break;
//                    default:
//                        i = 0;
//                        text = getContext().getResources().getString(R.string.test_text01);
//                        break;
//                }
//                i++;
//                button.setText(text);
//            }
//        });
//        layout.addView(button);
//        return layout;
//    }
}
