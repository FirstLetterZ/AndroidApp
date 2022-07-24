package com.zpf.app.launcher;

import com.zpf.app.activity.NetView;
import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;

/**
 * Created by ZPF on 2019/3/25.
 */

public class StartScreenActivity extends CompatContainerActivity{
    @Override
    protected Class<? extends IViewProcessor> defViewProcessorClass() {
        return NetView.class;
    }
}
