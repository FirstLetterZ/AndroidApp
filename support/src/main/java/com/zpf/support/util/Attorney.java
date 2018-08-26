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
    private HashMap<String, Record> archives = new HashMap<>();
//    private HashMap<String,Record> archives=new HashMap<>();

    public void join(Activity activity, boolean isNew,HeritageReceiver  heritageReceiver) {
        if (activity != null) {
            Record record = archives.get(activity.getClass().getName());
            if (record == null) {
                customerList.add(activity);
            } else {
                if (record.aList.size()>0) {
                    for(Heritage heritage:record.aList){
                        if(TextUtils.equals(heritage.successor,activity.getClass().getName())){
                            if(isNew==heritage.canBeNew){
                                if (heritage.data==null) {
                                    heritage.data=new Intent();
                                }
                                heritage.data.putExtra("from",heritage.from);
                                activity.onProvideAssistData(heritage.data.getBundleExtra(""));
                            }
                        }

                    }
                }
            }

        }
        //检查
        customerList.add(activity);
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


    class Heritage {
        private String successor;
        private Intent data;
        private int resultCode = 100;
        private String from;
        private boolean canBeNew;
    }

    class Testament {
        private List<String> killName = new LinkedList<>();
        private List<String> exception = new LinkedList<>();
        int killCount = -1;
        boolean killNew = false;

    }

    class Record {
        String onwer;
        int effective;
        List<Heritage> aList = new ArrayList<>();
        List<Testament> bList = new ArrayList<>();
    }

    interface HeritageReceiver{
        void onReceive(int cipher ,String from,Intent data);
    }
}
