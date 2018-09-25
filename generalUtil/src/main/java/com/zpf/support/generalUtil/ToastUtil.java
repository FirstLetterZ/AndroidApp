package com.zpf.support.generalUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;

/**
 * Created by ZPF on 2018/7/26.
 */
public class ToastUtil {
    private static SoftReference<ToastUtil> mUtil;
    private Toast mToast;
    private TextView mText;

    private static ToastUtil get() {
        if (mUtil == null || mUtil.get() == null) {
            synchronized (ToastUtil.class) {
                if (mUtil == null || mUtil.get() == null) {
                    mUtil = new SoftReference<>(new ToastUtil());
                }
            }
        }
        return mUtil.get();
    }

    private ToastUtil() {
        Context context = AppContext.get();
        try {
            mToast = new Toast(context);
            float density = context.getResources().getDisplayMetrics().density;

            LinearLayout toastLayout = new LinearLayout(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            toastLayout.setLayoutParams(params);
            toastLayout.setPadding((int) (20 * density), (int) (20 * density), (int) (20 * density), (int) (20 * density));
            toastLayout.setGravity(Gravity.CENTER);
            mText = new TextView(context);
            mText.setMinWidth((int) (50 * density));
            mText.setPadding((int) (16 * density), (int) (10 * density), (int) (16 * density), (int) (10 * density));
            mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            mText.setTextColor(Color.WHITE);
            mText.setGravity(Gravity.CENTER_HORIZONTAL);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(18 * density);
            gradientDrawable.setColor(Color.parseColor("#90000000"));
            mText.setBackground(gradientDrawable);
            toastLayout.addView(mText);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setView(toastLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //弹toast
    public static void toast(String msg) {
        try {
            get().mText.setText(msg);
            get().mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
