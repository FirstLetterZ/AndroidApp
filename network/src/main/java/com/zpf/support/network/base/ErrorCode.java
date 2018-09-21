package com.zpf.support.network.base;

/**
 * Created by ZPF on 2018/9/21.
 */

public @interface ErrorCode {
    int DATA_NULL = -900;
    int NO_SERVER_CODE = -901;
    int SSL_ERROR = -902;
    int PARSE_ERROR = -903;
    int ACCOUNT_ERROR = -904;
    int NETWORK_ERROR = -910;
    int CONNECT_ERROR = -911;
    int TIMEOUT_ERROR = -912;
    int INTERRUPTED_ERROR = -913;
    int IO_ERROR = -920;
}
