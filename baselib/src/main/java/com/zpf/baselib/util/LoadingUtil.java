package com.zpf.baselib.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpf.baselib.R;

/**
 * Created by ZPF on 2018/3/22.
 */
public class LoadingUtil {
    private static final int SHOW_LOAD = 3000;
    private static final int HIDE_LOAD = 3001;
    private static final int SHOW_MSG = 3002;
    private static final String LOADING_MSG = "数据加载中";
    private static final String MSG_KEY = "prompt";
    private LinearLayout progressLayout;
    private TextView tvLoadingPrompt;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOAD:
                    if (tvLoadingPrompt != null) {
                        tvLoadingPrompt.setText(LOADING_MSG);
                    }
                    if (progressLayout != null) {
                        progressLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case HIDE_LOAD:
                    if (progressLayout != null) {
                        progressLayout.setVisibility(View.GONE);
                    }
                    break;
                case SHOW_MSG:
                    Bundle data = msg.getData();
                    if (tvLoadingPrompt != null && data != null) {
                        tvLoadingPrompt.setText(data.getString(MSG_KEY, ""));
                    }
                    if (progressLayout != null) {
                        progressLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
            }
            return false;
        }
    });

    public LoadingUtil(Activity activity) {
        if (activity.getWindow() == null) {
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        if (progressLayout == null) {
            createLayout(activity);
        }
        if (progressLayout.getParent() == null) {
            decorView.addView(progressLayout);
        }
    }

    public synchronized void showLoading(String msg) {
        if (mHandler != null) {
            Message m = mHandler.obtainMessage();
            m.what = SHOW_MSG;
            Bundle b = new Bundle();
            b.putString(MSG_KEY, msg);
            m.setData(b);
            mHandler.sendMessage(m);
        }
    }

    public synchronized void showLoading() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(SHOW_LOAD);
        }
    }

    public synchronized void hideLoading() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(HIDE_LOAD);
        }
    }

    public boolean isLoading() {
        return progressLayout != null && progressLayout.getVisibility() == View.VISIBLE;
    }

    private void createLayout(Context context) {
        progressLayout = new LinearLayout(context);
        progressLayout.setGravity(Gravity.CENTER);
        progressLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        progressLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.progress_loding, progressLayout);
        progressLayout.setVisibility(View.GONE);
        tvLoadingPrompt = progressLayout.findViewById(R.id.tvLoadingPrompt_lib);
        progressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tvLoadingPrompt.setText(LOADING_MSG);
    }
}
