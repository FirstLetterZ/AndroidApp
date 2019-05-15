package com.zpf.support.single;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.zpf.frame.INavigator;

import java.util.LinkedList;

/**
 * Created by ZPF on 2019/5/15.
 */

public class FragmentStack implements INavigator<FragmentViewProcessor> {
    private LinkedList<FragmentElementInfo> stackList = new LinkedList<>();
    private OnStackEmptyListener emptyListener;
    private Activity parentActivity;


    public FragmentStack(OnStackEmptyListener emptyListener) {
        this.emptyListener = emptyListener;
    }

    @Override
    public void push(FragmentViewProcessor target, Bundle params, int requestCode) {
        String elementTag = target.getClass().getName() + "-" + target.getAlias();
        FragmentElementInfo lastElementInfo = stackList.peekLast();
        if (lastElementInfo != null) {
            if (TextUtils.equals(elementTag, lastElementInfo.tag)) {
                return;
            } else {
                lastElementInfo.state = StackElementState.STACK_INSIDE;
            }
        }
        FragmentElementInfo newElementInfo = new FragmentElementInfo();
        newElementInfo.requestCode = requestCode;
        newElementInfo.tag = elementTag;
        newElementInfo.instance = target;
        newElementInfo.state = StackElementState.STACK_TOP;
        stackList.add(newElementInfo);
    }

    @Override
    public void push(FragmentViewProcessor target, Bundle params) {
        this.push(target, params, -1);
    }

    @Override
    public void push(FragmentViewProcessor target) {
        this.push(target, null, -1);
    }

    @Override
    public void poll(int resultCode, Intent data) {
        FragmentElementInfo lastElementInfo = stackList.pollLast();
        if (lastElementInfo != null) {
            lastElementInfo.state = StackElementState.STACK_OUTSIDE;
            FragmentElementInfo newLastElementInfo = stackList.peekLast();
            if (newLastElementInfo != null) {
                newLastElementInfo.state = StackElementState.STACK_TOP;
                newLastElementInfo.instance.onActivityResult(lastElementInfo.requestCode, resultCode, data);
            }
        }
        if (stackList.size() == 0 && emptyListener != null) {
            emptyListener.onEmpty();
        }
    }

    @Override
    public void poll() {
        this.poll(-1, null);
    }

    @Override
    public void pollUntil(FragmentViewProcessor target, int resultCode, Intent data) {
        FragmentElementInfo lastElementInfo = stackList.pollLast();
        lastElementInfo.instance.onActivityResult();
    }

    @Override
    public void pollUntil(FragmentViewProcessor target) {

    }

    @Override
    public void remove(FragmentViewProcessor target, int resultCode, Intent data) {

    }

    @Override
    public void remove(FragmentViewProcessor target) {

    }


    class FragmentElementInfo {
        String tag;
        FragmentViewProcessor instance;
        int requestCode;
        int resultCode;
        Intent resultData;
        @StackElementState
        int state;

        public String getTag() {
            return tag;
        }

        public int getState() {
            return state;
        }
    }

}
