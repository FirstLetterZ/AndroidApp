package com.example.aplugin;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zpf.api.ILayoutId;
import com.zpf.support.base.ViewProcessor;
import com.zpf.tool.SafeClickListener;

@ILayoutId(R.layout.activity_main)
public class TestMainLayout extends ViewProcessor {
    Button button = (Button) find(R.id.btn_test);
    int i = 0;
    private View.OnClickListener btnClick = new SafeClickListener() {

        @Override
        public void click(View v) {
            push(TestSecondLayout.class);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button.setText("下一页");
        button.setOnClickListener(btnClick);
    }

}
