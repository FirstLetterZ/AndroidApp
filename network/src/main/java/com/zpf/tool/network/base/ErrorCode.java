package com.zpf.tool.network.base;

/**
 * Created by ZPF on 2018/9/21.
 */

public @interface ErrorCode {
    int RESPONSE_DEFAULT = 0;
    int RESPONSE_SUCCESS = 200;
    int DATA_NULL = 700;
    int NO_SERVER_CODE = 701;
    int SSL_ERROR = 702;
    int PARSE_ERROR = 703;
    int ACCOUNT_ERROR = 704;
    int HSOT_ERROR = 705;
    int SERVICE_ERROR = 705;
    int SOCKET_ERROR = 710;
    int CONNECT_ERROR = 711;
    int TIMEOUT_ERROR = 712;
    int INTERRUPTED_ERROR = 713;
    int IO_ERROR = 720;
    int RESPONSE_ILLEGAL = 730;

    int SAVE_LOCAL_FAIL = 801;
    int LOADING_NETWORK = 802;
    int LOAD_LOCAL_DATA = 803;
}
