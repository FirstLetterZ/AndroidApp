package com.zpf.support.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 未完成
 */
public class Attorney {
    private List<Activity> customerList = new LinkedList<>();
    private HashMap<String, Record> archives = new HashMap<>();//档案库（将被受理的申请）
    private HashMap<String, HeritageReceiver> receivers = new HashMap<>();//接收到对应遗产时的处理
    private HashMap<String, Record> applications = new HashMap<>();//申请表

    public void join(Activity activity, boolean isNew) {
        if (activity == null) {
            return;
        }
        customerList.add(activity);
        Record record = archives.get(activity.getClass().getName());
        if (record.effective>0) {
            record.effective--;
        }else{

        }
        if (record != null && record.aList.size() > 0) {
            for (Heritage heritage : record.aList) {
                if (TextUtils.equals(heritage.successor, activity.getClass().getName())) {
                    if (isNew == heritage.canBeNew) {
                        if (heritage.data == null) {
                            heritage.data = new Intent();
                        }
                        heritage.data.putExtra("from", heritage.from);
                        activity.onProvideAssistData(heritage.data.getBundleExtra(""));
                    }
                }
            }
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                customerList.add(activity);
            }
        }
    }

    public void deathProof(Activity activity) {
        //检查
        customerList.remove(activity);
    }

    public void addTestament(Activity activity, Testament testament) {
        if (customerList.contains(activity)) {

        }
    }


    public void addHeritage(Activity activity, Heritage heritage) {
        if (customerList.contains(activity)) {

        }
    }

    public void setEffectiveTime(int time) {

    }

    public boolean check(Activity activityName, boolean isNewCreate) {
        //如果在列表内则执行onActivityForResult
        return false;
    }


    //遗产
    class Heritage {
        String successor;//继承人
        Intent data;
        String from;//来源
        boolean canBeNew;//是否可新创建
    }

    //遗嘱
    class Testament {
        private List<String> killName = new LinkedList<>();
        private List<String> exception = new LinkedList<>();
        int killCount = -1;
        boolean killNew = false;
    }

    class Record {
        String onwer;//来源
        int effective;//有效次数
        List<Heritage> aList = new ArrayList<>();//全部遗产
//        List<Testament> bList = new ArrayList<>();
    }

    interface HeritageReceiver {
        void onReceive(int cipher, String from, Intent data);
    }
}
