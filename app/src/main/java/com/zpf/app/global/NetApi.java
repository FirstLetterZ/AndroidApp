package com.zpf.app.global;

import com.google.gson.JsonElement;
import com.zpf.tool.network.model.ResponseResult;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author Created by ZPF on 2021/3/19.
 */
public interface NetApi {

    @GET("/test/first")
    Call<ResponseResult<JsonElement>> firstCall();

    @GET("/test/second")
    Call<ResponseResult<JsonElement>> secondCall();

    @GET("/test/third")
    Call<ResponseResult<JsonElement>> thirdCall();
}
