package com.zpf.appLib.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;


public abstract class TimeTaskUtil {
    private Timer timer;
    private TimerTask timerTask;
    private long timeInterval = 3000;//间隔
    private boolean isRunning = false;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                doInMainThread();
            } else {
                super.handleMessage(msg);
            }
        }
    };

    //停止轮播
    public void stopPlay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        isRunning = false;
    }

    //开始轮播
    public void startPlay(long delay) {
        startPlay(delay, -99);
    }

    public void startPlay(long delay, long interval) {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    doInChildThread();
                    handler.sendEmptyMessage(0);
                }
            };
        }
        if (interval > 0) {
            timeInterval = interval;
        }
        if (isRunning) {
            return;
        }
        isRunning = true;
        try {
            timer.schedule(timerTask, delay, timeInterval);//轮播间隔
        } catch (Exception e) {
            isRunning = true;
            e.printStackTrace();
        }
    }

    protected abstract void doInMainThread();

    protected abstract void doInChildThread();

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }
}
