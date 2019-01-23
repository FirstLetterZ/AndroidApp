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
import com.zpf.tool.AutoSave;
import com.zpf.tool.AutoSaveUtil;
import com.zpf.tool.SafeClickListener;
import com.zpf.tool.permission.PermissionInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Handler handler = new Handler();
    private RefreshLayout refreshLayout;
    private RecyclerView rvTest;
    @AutoSave
    private int number;
    @AutoSave
    private SparseArray<PermissionInfo> stringSparseArray;
    @AutoSave
    private List<TestBaen> list;
    @AutoSave
    private List<String> testList;
    @AutoSave
    private TestBaen[] testBaens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AutoSaveUtil.restore(this, savedInstanceState);
        setContentView(R.layout.activity_main_re);
        findViewById(R.id.iv_icon).setOnClickListener(new SafeClickListener() {
            @Override
            public void click(View v) {
                startActivity(new Intent(v.getContext(), Main2Activity.class));
            }
        });
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        initValue();
        if (outState != null) {
            AutoSaveUtil.save(this, outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        AutoSaveUtil.restore(this, savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initValue() {
        stringSparseArray = new SparseArray<>();
        stringSparseArray.put(1, new PermissionInfo());
        list = new ArrayList<>();
        list.add(new TestBaen());
        testBaens = new TestBaen[]{new TestBaen(), new TestBaen()};
        number = 666;
        testList = new ArrayList<>();
        testList.add("1");
        testList.add("2");
    }
}
