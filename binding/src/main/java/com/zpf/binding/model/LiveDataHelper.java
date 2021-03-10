package com.zpf.binding.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.zpf.api.IGroup;
import com.zpf.api.OnDestroyListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LiveDataHelper {
    private final HashMap<LiveData<?>, HashSet<Observer<?>>> observerMap = new HashMap<>();

    public LiveDataHelper(IGroup<OnDestroyListener> destroyGroup) {
        if (destroyGroup != null) {
            destroyGroup.add(new OnDestroyListener() {
                @Override
                public void onDestroy() {
                    clearAll();
                }
            });
        }
    }

    public <T> void bindObserver(LiveData<T> data, Observer<T> observer) {
        HashSet<Observer<?>> search = observerMap.get(data);
        if (search == null) {
            search = new HashSet<>();
            search.add(observer);
            data.observeForever(observer);
            observerMap.put(data, search);
        } else if (!search.contains(observer)) {
            search.add(observer);
            data.observeForever(observer);
        }
    }

    public void clearObserver(LiveData data) {
        HashSet<Observer<?>> search = observerMap.get(data);
        if (search != null) {
            for (Observer<?> observer : search) {
                data.removeObserver(observer);
            }
            search.clear();
        }
    }

    public void clearAll() {
        HashSet<Observer<?>> search;
        LiveData data;
        for (Map.Entry<LiveData<?>, HashSet<Observer<?>>> entry : observerMap.entrySet()) {
            search = entry.getValue();
            if (search != null) {
                data = entry.getKey();
                for (Observer<?> observer : search) {
                    data.removeObserver(observer);
                }
            }
        }
        observerMap.clear();
    }
}
