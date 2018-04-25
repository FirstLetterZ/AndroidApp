package com.zpf.middleware.network.api;


import com.google.gson.JsonElement;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZPF on 2018/4/24.
 */

public interface UserApi {
    //获取验证码
    @POST("user/refreshToken")
    Observable<JsonElement> refreshToken(@Body RequestBody body);

}
