package com.zpf.support.network.base;

import retrofit2.Call;

public interface INetworkCallCreator<T> {
    Call<T> callNetwork();
}