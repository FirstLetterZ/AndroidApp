package com.zpf.tool.network.base;

public interface INetworkCallCreator<R, T> {
    T callNetwork(R param);
}