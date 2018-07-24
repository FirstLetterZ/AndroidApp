package com.zpf.baselib.interfaces;

import android.view.ViewGroup;


/**
 * Created by ZPF on 2018/6/13.
 */
public interface TitleBarInterface {

    IconText getLeftImage();

    IconText getLeftText();

    ViewGroup getLeftLayout();

    IconText getRightImage();

    IconText getRightText();

    ViewGroup getRightLayout();

    IconText getTitle();

    IconText getSubTitle();

    ViewGroup getTitleLayout();

    ViewGroup getLayout();
}