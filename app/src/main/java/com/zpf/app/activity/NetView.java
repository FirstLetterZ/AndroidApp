package com.zpf.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.zpf.api.OnDestroyListener;
import com.zpf.app.R;
import com.zpf.app.global.DataCall;
import com.zpf.app.global.NetApi;
import com.zpf.support.base.ViewProcessor;
import com.zpf.tool.network.base.INetworkCallCreator;
import com.zpf.tool.network.base.OnLoadingListener;
import com.zpf.tool.network.base.OnResponseListener;
import com.zpf.tool.network.model.RequestType;
import com.zpf.tool.network.model.ResponseResult;
import com.zpf.tool.network.request.MergeRequest;
import com.zpf.tool.network.request.NetRequest;
import com.zpf.tool.network.retrofit.RetrofitRequest;

import retrofit2.Call;

/**
 * @author Created by ZPF on 2021/3/19.
 */
public class NetView extends ViewProcessor {
    private NetRequest<ResponseResult<JsonElement>> netCall1 = new RetrofitRequest<>(new INetworkCallCreator<Call<ResponseResult<JsonElement>>>() {
        @Override
        public Call<ResponseResult<JsonElement>> callNetwork() {
            return DataCall.getApi(NetApi.class).firstCall();
        }
    }).setResponseListener(new OnResponseListener<ResponseResult<JsonElement>>() {
        @Override
        public void onResponse(boolean success, int code, ResponseResult<JsonElement> data, String msg) {
            tv01.setText(("success=" + success + ";code=" + code));

        }
    });
    private NetRequest<ResponseResult<JsonElement>> netCall2 = new RetrofitRequest<>(new INetworkCallCreator<Call<ResponseResult<JsonElement>>>() {
        @Override
        public Call<ResponseResult<JsonElement>> callNetwork() {
            return DataCall.getApi(NetApi.class).secondCall();
        }
    }).setResponseListener(new OnResponseListener<ResponseResult<JsonElement>>() {
        @Override
        public void onResponse(boolean success, int code, ResponseResult<JsonElement> data, String msg) {
            tv02.setText(("success=" + success + ";code=" + code));
        }
    });
    private NetRequest<ResponseResult<JsonElement>> netCall3 = new RetrofitRequest<>(new INetworkCallCreator<Call<ResponseResult<JsonElement>>>() {
        @Override
        public Call<ResponseResult<JsonElement>> callNetwork() {
            return DataCall.getApi(NetApi.class).thirdCall();
        }
    }).setResponseListener(new OnResponseListener<ResponseResult<JsonElement>>() {
        @Override
        public void onResponse(boolean success, int code, ResponseResult<JsonElement> data, String msg) {
            tv03.setText(("success=" + success + ";code=" + code));
        }
    });
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
        addListener(netCall1, OnDestroyListener.class);
        addListener(netCall2, OnDestroyListener.class);
        addListener(netCall3, OnDestroyListener.class);
        mergeRequest
                .merge(netCall1, RequestType.TYPE_DEFAULT)
                .merge(netCall2, RequestType.TYPE_DEFAULT)
                .merge(netCall3, RequestType.TYPE_DEFAULT);
        bind(R.id.btn_load1);
        bind(R.id.btn_load2);
        bind(R.id.btn_load3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load1:
                toLoad(-1);
                break;
            case R.id.btn_load2:
                toLoad(0);
                break;
            case R.id.btn_load3:
                toLoad(1);
                break;
        }
    }

    private void toLoad(int type) {
        tv01.setText("loading");
        tv02.setText("loading");
        tv03.setText("loading");
        mergeRequest.load(type);
    }
}
