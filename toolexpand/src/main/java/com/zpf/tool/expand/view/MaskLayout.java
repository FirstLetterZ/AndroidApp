package com.zpf.tool.expand.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import com.zpf.tool.expand.util.DataLoadStateCode;
import com.zpf.tool.expand.util.LoadStateListener;

/**
 * Created by ZPF on 2019/7/19.
 */
public class MaskLayout<T extends View> extends PackedLayout<T> {
    protected LinearLayout maskLayout;
    protected ProgressBar loadView;
    protected ImageView ivPicture;
    protected TextView tvMessage;
    protected Button btnRetry;
    private LoadStateListener stateListener;
    private final int maskViewKey = 1;

    public MaskLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public MaskLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //初始化全部要用的视图
    public void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int spaceHeight = (int) (10 * context.getResources().getDisplayMetrics().density);
        maskLayout = new LinearLayout(context);
        maskLayout.setGravity(Gravity.CENTER);
        maskLayout.setOrientation(LinearLayout.VERTICAL);
        loadView = new ProgressBar(context);
        ivPicture = new ImageView(context);
        tvMessage = new TextView(context);
        btnRetry = new Button(context);
        Space space01 = new Space(context);
        space01.setLayoutParams(new LinearLayout.LayoutParams(0, spaceHeight));
        Space space02 = new Space(context);
        space02.setLayoutParams(new LinearLayout.LayoutParams(0, spaceHeight));
        maskLayout.addView(loadView);
        maskLayout.addView(ivPicture);
        maskLayout.addView(space01);
        maskLayout.addView(tvMessage);
        maskLayout.addView(space02);
        maskLayout.addView(btnRetry);
        addChildByKey(maskViewKey, maskLayout);
    }

    public void changeState(@DataLoadStateCode int stateCode) {
        switch (stateCode) {
            case DataLoadStateCode.EMPTY:
                showEmpty();
                break;
            case DataLoadStateCode.ERROR:
                showError();
                break;
            case DataLoadStateCode.LOADING:
                showLoading();
                break;
            case DataLoadStateCode.SUCCESS:
                showSuccess();
                break;
            case DataLoadStateCode.UNDEFINED:
                if (stateListener != null) {
                    stateListener.onStateChanged(DataLoadStateCode.UNDEFINED);
                }
                break;
        }
    }

    public void showLoading() {
        showChildByKey(maskViewKey);
        loadView.setVisibility(View.VISIBLE);
        ivPicture.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.GONE);
        if (stateListener != null) {
            stateListener.onStateChanged(DataLoadStateCode.LOADING);
        }
    }

    public void showEmpty() {
        showChildByKey(maskViewKey);
        loadView.setVisibility(View.GONE);
        ivPicture.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
        if (stateListener != null) {
            stateListener.onStateChanged(DataLoadStateCode.EMPTY);
        }
    }

    public void showError() {
        showChildByKey(maskViewKey);
        loadView.setVisibility(View.GONE);
        ivPicture.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
        if (stateListener != null) {
            stateListener.onStateChanged(DataLoadStateCode.ERROR);
        }
    }

    public void showSuccess() {
        showContentView();
        if (stateListener != null) {
            stateListener.onStateChanged(DataLoadStateCode.SUCCESS);
        }
    }

    public void setStateListener(LoadStateListener stateListener) {
        this.stateListener = stateListener;
    }

    public ProgressBar getLoadView() {
        return loadView;
    }

    public ImageView getPicture() {
        return ivPicture;
    }

    public TextView getMessage() {
        return tvMessage;
    }

    public Button getButton() {
        return btnRetry;
    }

}