package com.zpf.app.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.zpf.app.R;
import com.zpf.refresh.util.OnRefreshListener;
import com.zpf.refresh.util.RefreshLayoutType;
import com.zpf.refresh.view.RefreshLayout;
import com.zpf.tool.MainHandler;
import com.zpf.tool.SafeClickListener;
import com.zpf.tool.ToastUtil;
import com.zpf.tool.config.GlobalConfigImpl;
import com.zpf.tool.config.GlobalConfigInterface;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Handler handler = new Handler();
//    private RefreshLayout refreshLayout;
    private PackedRecyclerView rvTest;
    List<String> testList = new ArrayList<>();
    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalConfigImpl.get().init(getApplication(), new GlobalConfigInterface() {
            @Override
            public boolean isDebug() {
                return true;
            }

            @Override
            public Application getApplication() {
                return MainActivity.this.getApplication();
            }

            @Override
            public void onObjectInit(Object object) {

            }

            @Override
            public Object invokeMethod(Object object, String methodName, Object... args) {
                return null;
            }

            @Override
            public <T> T getGlobalInstance(Class<T> target) {
                return null;
            }
        });
        setContentView(R.layout.activity_main_re);
//        refreshLayout = findViewById(R.id.rl_test);
//        refreshLayout.setType(RefreshLayoutType.BOTH_UP_DOWN);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.setRefreshResult(true);
//                    }
//                }, 1000);
//            }
//
//            @Override
//            public void onLoadMore() {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.setRefreshResult(true);
//                    }
//                }, 1000);
//            }
//        });
        rvTest = findViewById(R.id.prv_test);
//        rvTest.setLayoutManager(new LinearLayoutManager(this));
        int i = 1;
        while (i < 20) {
            testList.add("测试条目第" + i + "条");
            i++;
        }
        DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(String oldItem, String newItem) {
                return TextUtils.equals(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(String oldItem, String newItem) {
                return TextUtils.equals(oldItem, newItem);
            }
        };
        adapter = new TestAdapter(diffCallback);
        rvTest.getContentView().setAdapter(adapter);

        findViewById(R.id.ll_nav).setOnClickListener(new SafeClickListener() {
            @Override
            public void click(View v) {
                Log.e("ZPF_DEBUG", "getMeasuredHeight=" + rvTest.getContentView().getMeasuredHeight());
            }
        });
        findViewById(R.id.iv_icon).setOnClickListener(new SafeClickListener() {
            @Override
            public void click(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
        adapter.submitList(testList);
    }

}
