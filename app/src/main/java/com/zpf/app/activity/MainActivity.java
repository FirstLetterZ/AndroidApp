package com.zpf.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.zpf.app.R;
import com.zpf.refresh.util.OnRefreshListener;
import com.zpf.refresh.util.RefreshLayoutType;
import com.zpf.refresh.view.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Handler handler = new Handler();
    private RefreshLayout refreshLayout;
    private RecyclerView rvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_re);
        refreshLayout = findViewById(R.id.rl_test);
        refreshLayout.setType(RefreshLayoutType.BOTH_UP_DOWN);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshResult(true);
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshResult(true);
                    }
                }, 1000);
            }
        });
        rvTest = findViewById(R.id.rv_test);
        rvTest.setLayoutManager(new LinearLayoutManager(this));
        rvTest.setAdapter(new TestAdapter());
    }

}
