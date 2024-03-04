package com.zpf.tool.expand.event;


import androidx.annotation.NonNull;

import com.zpf.api.IChecker;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public class SimpleSender<T> {
    protected final ConcurrentHashMap<String, IReceiver<T>> receivers = new ConcurrentHashMap<>();
    protected final HashSet<IChecker<T>> checkers = new HashSet<>();
    protected String recordToken = UUID.randomUUID().toString();

    public boolean addChecker(IChecker<T> checker) {
        if (checker != null) {
            return checkers.add(checker);
        }
        return false;
    }

    public boolean removeChecker(IChecker<T> checker) {
        if (checker != null) {
            return checkers.remove(checker);
        }
        return false;
    }

    public boolean register(IReceiver<T> receiver) {
        if (receiver == null) {
            return false;
        }
        String name = receiver.name();
        if (name == null || name.length() == 0) {
            return false;
        }
        receivers.put(name, receiver);
        return true;
    }

    public boolean unregister(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        return receivers.remove(name) != null;
    }

    public boolean send(T t, String... targets) {
        if (t == null) {
            return false;
        }
        boolean illegal = false;
        for (IChecker<T> c : checkers) {
            if (!c.check(t)) {
                illegal = true;
                break;
            }
        }
        if (illegal) {
            return false;
        }
        ITransRecord record = new SimpleRecord(recordToken);
        if (targets == null || targets.length == 0) {
            for (Map.Entry<String, IReceiver<T>> entry : receivers.entrySet()) {
                entry.getValue().onReceive(t, record);
                record.addReader(entry.getKey(), recordToken);
                if (record.isInterrupted()) {
                    break;
                }
            }
        } else {
            LinkedList<String> unHandList = null;
            for (String name : targets) {
                IReceiver<T> receiver = receivers.get(name);
                if (receiver == null) {
                    if (unHandList == null) {
                        unHandList = new LinkedList<>();
                    }
                    unHandList.add(name);
                } else {
                    receiver.onReceive(t, record);
                    record.addReader(receiver.name(), recordToken);
                    if (record.isInterrupted()) {
                        break;
                    }
                }
            }
            if (!record.isInterrupted() && unHandList != null) {
                onSendFail(t, unHandList, record);
            }
        }
        return true;
    }

    protected void onSendFail(@NonNull T t, @NonNull List<String> names, @NonNull ITransRecord record) {

    }

}
