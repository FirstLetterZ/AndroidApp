//package com.zpf.appLib.util;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.util.Log;
//
//import com.bocs.bmc.activity.UploadExceptionActivity;
//import com.bocs.bmc.commons.AppValue;
//import com.bocs.bmc.commons.Constants;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class CrashHandler implements Thread.UncaughtExceptionHandler {
//    private static CrashHandler mCrashHandler;
//    private Handler mHandler = null;
//
//    public static CrashHandler getInstance() {
//        if (mCrashHandler == null) {
//            mCrashHandler = new CrashHandler();
//            Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
//        }
//        return mCrashHandler;
//    }
//
//    @Override
//    public void uncaughtException(Thread thread, Throwable throwable) {
//        if (mHandler == null) {
//            HandlerThread handlerThread = new MyHandlerThread("myHandlerThread");
//            handlerThread.start();
//            mHandler = new Handler(handlerThread.getLooper(), (Handler.Callback) handlerThread);
//        }
//        final String error = Log.getStackTraceString(throwable);
//        LogUtil.e(error);
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                File file = buildLogFile();
//                if (file.exists()) {
//                    addContent(file, error);
//                    mHandler.sendEmptyMessage(0);
//                } else {
//                    PublicUtil.toast("文件创建失败！请检查SD卡是否插好，读写权限是否开启,应用即将关闭");
//                    mHandler.sendEmptyMessage(2);
//                }
//            }
//        });
//    }
//
//    private File buildLogFile() {
//        File file = new File(Constants.LOG_ROOT_PATH);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        File logFile = new File(Constants.LOG_ROOT_PATH, "ErrorLogAndroid.txt");
//        if (!logFile.exists()) {
//            try {
//                if (logFile.createNewFile()) {
//                    writeTitle(logFile);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return logFile;
//    }
//
//    /**
//     * 追加文件：使用FileWriter
//     */
//    private void addContent(File file, String content) {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss", Locale.getDefault());
//        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String data = formatter.format(curDate);
//        String str = content.replace("\n", "\r\n");
//        try {
//            // 打开一个FileWriter，追加书写
//            FileWriter writer = new FileWriter(file, true);
//            writer.write("\r\n");
//            writer.write(data);
//            writer.write("\r\n");
//            writer.write(str);
//            writer.close();
//        } catch (IOException e) {
//            LogUtil.e("写入文件失败：" + e);
//        }
//    }
//
//    private void writeTitle(File file) {
//        String content = "手机型号:" + android.os.Build.MODEL
//                + "\r\n产品型号:" + android.os.Build.PRODUCT
//                + "\r\nCPU类型:" + android.os.Build.CPU_ABI
//                + "\r\n系统版本:" + android.os.Build.VERSION.RELEASE
//                + "\r\nSDK版本号:" + android.os.Build.VERSION.SDK_INT
//                + "\r\n系统定制商:" + android.os.Build.BRAND
//                + "\r\nROM制造商:" + android.os.Build.MANUFACTURER
//                + "\r\n";
//        try {
//            // 打开一个FileWriter，覆盖书写
//            FileWriter writer = new FileWriter(file, false);
//            writer.write(content);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private class MyHandlerThread extends HandlerThread implements Handler.Callback {
//
//        MyHandlerThread(String name) {
//            super(name);
//        }
//
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    Intent intent = new Intent(AppValue.getInstance().getApplicationContext(), UploadExceptionActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    AppValue.getInstance().startActivity(intent);
//                    mHandler.sendEmptyMessage(99);
//                    break;
//                case 1:
//                    break;
//                case 2:
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mHandler.sendEmptyMessage(99);
//                        }
//                    }, 1000);
//                    break;
//                case 99:
//                    try {
//                        AppValue.getInstance().exit();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                    break;
//            }
//            return true;
//        }
//    }
//}
