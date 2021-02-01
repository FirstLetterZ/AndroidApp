package com.zpf.tool.expand.cache;

import android.content.SharedPreferences;

import com.zpf.api.IStorageQueue;

public class SpStorageQueue implements IStorageQueue<String> {
    SharedPreferences.Editor editor;
    SpStorageWorker storageWorker;

    SpStorageQueue(SpStorageWorker spStorageWorker) {
        this.storageWorker = spStorageWorker;
    }

    @Override
    public SpStorageQueue add(String name, Object value) {
        editor = storageWorker.add(editor, name, value);
        return this;
    }

    public boolean commit() {
        if (editor != null) {
            editor.commit();
        }
        return false;
    }
}