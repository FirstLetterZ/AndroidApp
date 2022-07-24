package com.zpf.app.activity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.api.IResultBean;
import com.zpf.api.OnDataResultListener;
import com.zpf.app.R;
import com.zpf.file.FileUriUtil;
import com.zpf.support.base.ViewProcessor;
import com.zpf.tool.expand.download.DownloadListener;
import com.zpf.tool.expand.download.DownloadUtil;
import com.zpf.tool.network.base.OnLoadingListener;
import com.zpf.tool.network.request.MergeRequest;
import com.zpf.tool.network.util.OkHttpNetUtil;

import java.io.File;
import java.util.Map;

/**
 * @author Created by ZPF on 2021/3/19.
 */
public class NetView extends ViewProcessor {
    private final DownloadListener listener01 = new DownloadListener() {
        @Override
        public void onResult(boolean success, @Nullable Uri data) {
            tv01.setText("success=" + success);
        }

        @Override
        public void onProgress(long total, long current, @Nullable Object target) {
            tv01.setText("progress=" + (current * 100f / total));
        }
    };
    private final OnDataResultListener<IResultBean<String>> resultListener = new OnDataResultListener<IResultBean<String>>() {
        @Override
        public void onResult(boolean success, @Nullable @org.jetbrains.annotations.Nullable IResultBean<String> data) {
            tv01.setText("success=" + success);
        }
    };

    private final DownloadListener listener02 = new DownloadListener() {
        @Override
        public void onResult(boolean success, @Nullable Uri data) {
            tv02.setText("success=" + success);
        }

        @Override
        public void onProgress(long total, long current, @Nullable Object target) {
            tv02.setText("progress=" + (current * 100f / total));
        }
    };
    private TextView tv01 = find(R.id.tv01);
    private TextView tv02 = find(R.id.tv02);
    private TextView tv03 = find(R.id.tv03);
    private MergeRequest mergeRequest = new MergeRequest(new OnLoadingListener() {
        @Override
        public void onLoading(boolean loadingData) {
            if (loadingData) {
                mContainer.showLoading();
            } else {
                mContainer.hideLoading();
            }
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.layout_net_test;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind(R.id.btn_load1);
        bind(R.id.btn_load2);
        bind(R.id.btn_load3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load1:
                OkHttpNetUtil.download("https://book.kotlincn.net/kotlincn-docs.pdf",
                        FileUriUtil.pathToUri(getContext(), new File(getContext().getCacheDir(), "001.pdf")),
                        null, 0, resultListener, listener01, null);
                break;
            case R.id.btn_load2:
                DownloadUtil.get(getContext()).download(buildRequest("https://book.kotlincn.net/kotlincn-docs.pdf",
                        "test_download.pdf", null), listener02);
                break;
            case R.id.btn_load3:
//                toLoad(1);
                break;
        }
    }

    public DownloadManager.Request buildRequest(
            @NonNull String url, @NonNull String name, @Nullable Map<String, String> heads) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> entry : heads.entrySet()) {
                request.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        return request;
    }

    private void toLoad(int type) {
        tv01.setText("loading");
        tv02.setText("loading");
        tv03.setText("loading");
        mergeRequest.load(type);
    }
}
