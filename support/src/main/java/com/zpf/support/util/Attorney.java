package com.zpf.support.util;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Attorney {
    private List<Activity> customerList = new LinkedList<>();
    private HashMap<String,Record> archives=new HashMap<>();
//    private HashMap<String,Record> archives=new HashMap<>();

    public void join(Activity activity, boolean isNew) {
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
}
