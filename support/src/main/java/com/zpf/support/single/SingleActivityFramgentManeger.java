package com.zpf.support.single;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zpf.frame.INavigator;
import com.zpf.support.base.ViewProcessor;

import java.util.LinkedList;

/**
 * Created by ZPF on 2019/5/13.
 */

public class SingleActivityFramgentManeger implements INavigator<Class<? extends ViewProcessor>>{
    private LinkedList<Fragment>  fragmentStack=new LinkedList<>();
    private FragmentManager fragmentManager;

    public SingleActivityFramgentManeger(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
    public void pushNewActivity(Class target){

    }
    public void pushNewActivity(Fragment form,Class target){

    }

    @Override
    public void navigate(Class<? extends ViewProcessor> target) {
        fragmentManager.beginTransaction();
       Fragment fragment= fragmentManager.findFragmentByTag(target.getName());
    }

    @Override
    public void navigate(Class<? extends ViewProcessor> target, Bundle params) {

    }

    @Override
    public void navigate(Class<? extends ViewProcessor> target, Bundle params, int requestCode) {

    }


}
