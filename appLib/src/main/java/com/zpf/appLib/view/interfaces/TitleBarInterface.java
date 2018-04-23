package com.zpf.appLib.view.interfaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ZPF on 2018/4/16.
 */

public interface TitleBarInterface {
    TextView getFirstLeftText();

    TextView getSecondLeftText();

    View getLeftLayout();

    ImageView getLeftImage();

    TextView getFirstRightText();

    TextView getSecondRightText();

    ImageView getRightImage();

    View getRightLayout();

    TextView getTitle();

    TextView getSubTitle();

    View getTitleLayout();
}
