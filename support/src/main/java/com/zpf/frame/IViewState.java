package com.zpf.frame;

public interface IViewState {
    int getStateCode();

    boolean isLiving();//已创建到销毁之间的状态

    boolean isInteractive(); //可交互的状态

    boolean isVisible(); //用户可见的状态
}