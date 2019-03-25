package com.zpf.app.launcher;

import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;
import com.zpf.support.util.LogUtil;

/**
 * Created by ZPF on 2019/3/25.
 */

public class StartScreenActivity extends CompatContainerActivity {
    @Override
    protected IViewProcessor unspecifiedViewProcessor() {
        LogUtil.setLogOut(true);
        return new StartScreenView();
    }
}
