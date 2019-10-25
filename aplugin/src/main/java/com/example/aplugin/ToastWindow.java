package com.example.aplugin;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ToastWindow {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams layoutParams;
    private LinearLayout mLayout;
    private TextView defText;
    private volatile boolean destroyed;
    private int showTime = 3;
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (!destroyed) {
                    final CharSequence text = workQueue.take();
                    mLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mLayout.setVisibility(View.VISIBLE);
                            defText.setText(text);
                        }
                    });
                    LockSupport.parkNanos(ToastWindow.this, TimeUnit.SECONDS.toNanos(3));
                    if (workQueue.size() == 0) {
                        mLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                }

            } catch (Exception e) {
                destroy();
            }
        }
    });
    private BlockingQueue<CharSequence> workQueue = new LinkedBlockingQueue<>(100);

    public ToastWindow(Application application) {
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        float density = Resources.getSystem().getDisplayMetrics().density;

        defText = new TextView(application);
        defText.setMinWidth((int) (80 * density));
        defText.setPadding((int) (12 * density), (int) (8 * density), (int) (12 * density), (int) (8 * density));
        defText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        defText.setTextColor(Color.WHITE);
        defText.setGravity(Gravity.CENTER_HORIZONTAL);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(12 * density);
        gradientDrawable.setColor(Color.parseColor("#aa00000"));
        defText.setBackground(gradientDrawable);

        mLayout = new LinearLayout(application);
        mLayout.setEnabled(false);
        mLayout.setFocusable(false);
        mLayout.addView(getDefaultChild());
        mLayout.setVisibility(View.GONE);
        mLayout.setGravity(Gravity.CENTER);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowManager.addView(mLayout, layoutParams);
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public ViewGroup getRootLayout() {
        return mLayout;
    }

    public TextView getDefaultChild() {
        return defText;
    }

    public void toast(CharSequence text) {
        if (destroyed) {
        }
        workQueue.offer(text);
    }

    public void init() {
        if (thread != null) {
            if (!thread.isAlive()) {
                try {
                    thread.interrupt();
                } catch (Exception e) {
                    //
                } finally {
                    thread = null;
                }
            }
        }
        if (thread == null) {
            thread = createThread();
            thread.start();
        }
        destroyed = false;
    }

    public void destroy() {
        destroyed = true;
        thread = null;
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!destroyed) {
                        final CharSequence text = workQueue.take();
                        mLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mLayout.setVisibility(View.VISIBLE);
                                defText.setText(text);
                            }
                        });
                        LockSupport.parkNanos(ToastWindow.this, TimeUnit.MILLISECONDS.toNanos(100));
                        if (workQueue.size() == 0) {
                            mLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    mLayout.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    destroy();
                }

            }
        });
    }
}
