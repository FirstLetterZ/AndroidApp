package com.zpf.tool.expand.event;

import android.util.LruCache;

import androidx.annotation.NonNull;

import com.zpf.api.IReceiver;
import com.zpf.api.ITransRecord;

import java.util.List;

/**
 * @author Created by ZPF on 2021/8/12.
 */
public class RetrySender<T> extends SimpleSender<T> {
    protected final LruCache<String, Cache> waitHandlerMap;

    public RetrySender(int cacheSize) {
        if (cacheSize > 0) {
            waitHandlerMap = new LruCache<>(cacheSize);
        } else {
            waitHandlerMap = new LruCache<>(16);
        }
    }

    @Override
    public boolean register(IReceiver<T> receiver) {
        boolean result = super.register(receiver);
        if (result) {
            String receiverName=receiver.name();
            Cache cache = waitHandlerMap.remove(receiverName);
            if (cache != null) {
                try {
                    if (!cache.record.isInterrupted()) {
                        receiver.onReceive(cache.t, cache.record);
                        cache.record.addReader(receiverName, recordToken);
                    }
                    if (cache.record.isInterrupted()) {
                        for (String rn : cache.names) {
                            waitHandlerMap.remove(rn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    protected void onSendFail(@NonNull T t, @NonNull List<String> names, @NonNull ITransRecord record) {
        Cache cache = new Cache(t, names, record);
        for (String it : names) {
            waitHandlerMap.put(it, cache);
        }
    }

    private class Cache {
        private final T t;
        private final List<String> names;
        private final ITransRecord record;

        public Cache(T t, List<String> names, ITransRecord record) {
            this.t = t;
            this.names = names;
            this.record = record;
        }
    }
}
