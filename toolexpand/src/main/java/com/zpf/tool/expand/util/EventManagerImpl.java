package com.zpf.tool.expand.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.LruCache;

import com.zpf.api.IEvent;
import com.zpf.api.IEventManager;
import com.zpf.api.IFunction1;

import java.util.concurrent.ConcurrentHashMap;

public class EventManagerImpl implements IEventManager {
    private EventManagerImpl() {
    }

    private static class Instance {
        private static final EventManagerImpl mInstance = new EventManagerImpl();
    }

    public static EventManagerImpl get() {
        return Instance.mInstance;
    }

    private final ConcurrentHashMap<String, IFunction1<IEvent<Object>>> receiverMap = new ConcurrentHashMap<>();
    private final LruCache<String, IEvent<Object>> waitHandlerMap = new LruCache<>(16);

    @Override
    public void register(@NonNull String receiverName, @Nullable IFunction1<IEvent<Object>> receiver) {
        if (!TextUtils.isEmpty(receiverName) && receiver != null) {
            receiverMap.put(receiverName, receiver);
            IEvent<Object> waitHandleEvent = waitHandlerMap.get(receiverName);
            if (waitHandleEvent != null) {
                receiver.func(waitHandleEvent);
            }
        }
    }

    @Override
    public void unregister(@NonNull String receiverName) {
        if (!TextUtils.isEmpty(receiverName)) {
            receiverMap.remove(receiverName);
        }
    }

    @Override
    public void post(@Nullable String receiverName, @Nullable IEvent<Object> event) {
        handleEvent(receiverName, event, false);
    }

    @Override
    public void postInfallible(@Nullable String receiverName, @Nullable IEvent<Object> event) {
        handleEvent(receiverName, event, true);
    }

    private void handleEvent(@Nullable String receiverName, @Nullable IEvent<Object> event, boolean infallible) {
        boolean noHandler = true;
        if (TextUtils.isEmpty(receiverName)) {
            for (IFunction1<IEvent<Object>> handler : receiverMap.values()) {
                if (handler != null) {
                    handler.func(event);
                    noHandler = false;
                }
            }
        } else {
            IFunction1<IEvent<Object>> receiver = receiverMap.get(receiverName);
            if (receiver != null) {
                receiver.func(event);
                noHandler = false;
            }
        }
        if (infallible && noHandler && !TextUtils.isEmpty(receiverName)) {
            waitHandlerMap.put(receiverName, event);
        }
    }

}