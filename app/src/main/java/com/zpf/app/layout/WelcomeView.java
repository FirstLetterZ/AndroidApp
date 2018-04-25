package com.zpf.app.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zpf.app.R;
import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.base.BaseViewLayout;
import com.zpf.middleware.constants.StringConst;

/**
 * Created by ZPF on 2018/4/14.
 */
public class WelcomeView extends BaseViewLayout {
    private Button btnNext = $(R.id.btn_next);

    public WelcomeView(BaseViewContainer container) {
        super(container);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContainer().pushActivity(StringConst.VIEW_CLASS_INTRODUCE);
                getContainer().finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.welcome_layout;
    }
}
