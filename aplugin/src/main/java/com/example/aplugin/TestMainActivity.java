package com.example.aplugin;

import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.CompatContainerActivity;

public class TestMainActivity extends CompatContainerActivity {

    @Override
    protected Class<? extends IViewProcessor> defViewProcessorClass() {
        return TestMainLayout.class;
    }
}
