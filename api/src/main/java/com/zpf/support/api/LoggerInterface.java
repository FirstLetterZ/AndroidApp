package com.zpf.support.api;

/**
 * Created by ZPF on 2018/10/26.
 */

public interface LoggerInterface {

    void d(String tag, String content);

    void i(String tag, String content);

    void w(String tag, String content);

    void e(String tag, String content);
}
