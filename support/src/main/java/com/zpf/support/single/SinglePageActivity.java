package com.zpf.support.single;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zpf.frame.IViewProcessor;
import com.zpf.support.base.ContainerActivity;

import java.util.HashMap;

/**
 * Created by ZPF on 2019/5/20.
 */
public abstract class SinglePageActivity extends ContainerActivity {
    private FragmentStackManager fragmentStackManager;
    private static HashMap<String, FragmentStackManager> shareBox = new HashMap<>();

    @Override
    protected IViewProcessor initViewProcessor() {
        return null;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        int viewId = View.generateViewId();
        frameLayout.setId(viewId);
        setContentView(frameLayout);
        fragmentStackManager = new FragmentStackManager(getFragmentManager(), viewId);
        ShareBox.putRealNavigator("" + viewId, fragmentStackManager);
        fragmentStackManager.setEmptyListener(new OnStackEmptyListener() {
            @Override
            public void onEmpty() {
                finish();
            }
        });
        fragmentStackManager.push(launcher());
    }

    @Override
    public void onBackPressed() {
        fragmentStackManager.poll();
        super.onBackPressed();
    }

    public abstract Class<? extends FragmentViewProcessor> launcher();
}
