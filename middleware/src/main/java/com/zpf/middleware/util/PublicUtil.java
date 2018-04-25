package com.zpf.middleware.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zpf.appLib.constant.AppConst;


/**
 * Created by ZPF on 2018/4/17.
 */

public class PublicUtil {


    public static void toast(String s) {
        Toast.makeText(AppConst.instance().getApplication(), s, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int id) {
        Toast.makeText(AppConst.instance().getApplication(),
                AppConst.instance().getApplication().getString(id), Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, int id) {
        Toast.makeText(context, context.getString(id), Toast.LENGTH_SHORT).show();
    }

    public static int getColor(int resId) {
        return ContextCompat.getColor(AppConst.instance().getApplication(), resId);
    }

    //收起软键盘
    public static void packUpKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            if (activity.getCurrentFocus() != null) {
                try {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //弹出软键盘
    public static void showKeyboard(Activity activity) {
        /*如果顶部视图可见高度大于2/3屏幕高度，则认定软键盘未弹出（大约计算）*/
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        if ((rect.bottom - rect.top) > (0.667 * activity.getResources().getDisplayMetrics().heightPixels)) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    //获取APP版本号
    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo != null) {
            return packInfo.versionCode;
        } else {
            return 0;
        }
    }
}
