package com.zpf.login.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zpf.appLib.base.BaseViewContainer;
import com.zpf.appLib.base.BaseViewLayout;

/**
 * 功能介绍页
 * Created by ZPF on 2018/4/23.
 */
public class IntroduceView extends BaseViewLayout {
    public IntroduceView(BaseViewContainer container) {
        super(container);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }
}
