package com.zpf.tool.expand.event;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public class SimpleRecord implements ITransRecord {

    private volatile boolean interrupted;
    private final String token;
    private String operator;
    private final HashSet<String> reader = new HashSet<>();

    public SimpleRecord(String token) {
        this.token = token;
    }

    @Override
    public boolean addReader(@NonNull String name, @Nullable String token) {
        if (TextUtils.equals(this.token, token)) {
            reader.add(name);
            return true;
        }
        return false;
    }

    @Override
    public int readTime() {
        return reader.size();
    }

    @Override
    public boolean isTargetReceived(@NonNull String name) {
        if (reader.size() == 0) {
            return false;
        }
        return reader.contains(name);
    }

    @Override
    public void interrupt(@Nullable String operator) {
        if (!interrupted) {
            interrupted = true;
            this.operator = operator;
        }
    }

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    @Nullable
    @Override
    public String whoInterrupt() {
        return operator;
    }

}