package com.zpf.middleware.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
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

    public static String getString(int resId) {
        return AppConst.instance().getApplication().getString(resId);
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
