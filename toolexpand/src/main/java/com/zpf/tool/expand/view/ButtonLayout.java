package com.zpf.tool.expand.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ButtonLayout extends RelativeLayout {
    private boolean isMove = false;
    private long lastDown = 0L;
    private float downX = 0f;
    private float downY = 0f;
    private float touchAlpha = 0.8f;
    private float originalAlpha = 1;

    public ButtonLayout(Context context) {
        super(context);
    }

    public ButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                setAlpha(touchAlpha);
                isMove = false;
                lastDown = System.currentTimeMillis();
                downX = ev.getRawX();
                downY = ev.getRawY();
            }
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(downX - ev.getRawX()) > 3 || Math.abs(downY - ev.getRawY()) > 3) {
                    isMove = true;
                }
            }
            case MotionEvent.ACTION_UP: {
                setAlpha(originalAlpha);
                long dt = System.currentTimeMillis() - lastDown;
                if (!isMove && dt < 800) {
                    if (callOnClick()) {
                        return true;
                    }
                }
            }
            case MotionEvent.ACTION_CANCEL:
                setAlpha(originalAlpha);
            case MotionEvent.ACTION_OUTSIDE:
                setAlpha(originalAlpha);
        }
        return super.dispatchTouchEvent(ev);
    }

    public float getTouchAlpha() {
        return touchAlpha;
    }

    public void setTouchAlpha(float touchAlpha) {
        this.touchAlpha = touchAlpha;
    }
}