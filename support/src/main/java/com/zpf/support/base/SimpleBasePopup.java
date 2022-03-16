package com.zpf.support.base;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @author Created by ZPF on 2022/1/12.
 */
public abstract class SimpleBasePopup extends PopupWindow {
    private final Context mContext;
    private boolean hasCreated = false;

    public SimpleBasePopup(Context context, String[] items) {
        super(context);
        mContext = context;
        setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (!hasCreated && !isShowing() && getContentView() == null) {
            onCreate();
            hasCreated = true;
        }
        super.showAtLocation(parent, gravity, x, y);
        if (isShowing()) {
            onStart();
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (!hasCreated && !isShowing() && getContentView() == null) {
            onCreate();
            hasCreated = true;
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        if (isShowing()) {
            onStart();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (!isShowing()) {
            onStop();
        }
    }

    protected void onCreate() {
        View contentView = onCreateView(mContext);
        if (contentView != null) {
            contentView.measure(getWidth(), getHeight());
            int contentHeight = contentView.getMeasuredHeight();
            int contentWidth = contentView.getMeasuredWidth();
            setWidth(contentWidth);
            setHeight(contentHeight);
        }
        setContentView(contentView);
    }

    protected void onStart() {

    }

    protected void onStop() {

    }

    protected abstract View onCreateView(Context context);

}