package com.zpf.support.generalUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by ZPF on 2018/7/26.
 */

public class PublicUtil {

    public static int getColor(int color) {
        return AppContext.get().getResources().getColor(color);
    }

    public static String getString(int id) {
        return AppContext.get().getResources().getString(id);
    }

    public static <T> Class<T> getViewClass(Class<?> klass) {
        Type type = klass.getGenericSuperclass();
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) return null;
        return (Class<T>) types[0];
    }

    public static int getVersionCode(Context context) {
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        String versionName = "0.0.1";
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                versionName = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    public static String getAppName(Context context) {
        String name = "当前应用";
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                name = info.packageName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public static String getMoneyValue(BigDecimal bigDecimal, int scale) {
        if (bigDecimal == null) {
            return "";
        }
        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 获取设备识别id
     *
     * @return 如果返回null则代表缺少权限，若返回"unknown"代表获取失败
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceId(@NonNull Context context) {
        String result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result = Build.getSerial();
            } else {
                result = Build.SERIAL;
            }
        } else {
            result = Build.SERIAL;
        }
        if (TextUtils.isEmpty(result) || "unknown".equalsIgnoreCase(result)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                result = telephonyManager.getDeviceId();
            }
        }
        if (TextUtils.isEmpty(result)) {
            result = "unknown";
        }
        return result;
    }
}
