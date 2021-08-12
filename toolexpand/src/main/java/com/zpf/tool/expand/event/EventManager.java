package com.zpf.tool.expand.event;

import androidx.annotation.NonNull;

import com.zpf.api.ITransRecord;

import java.util.List;

/**
 * @author Created by ZPF on 2021/8/12.
 */
public class EventManager extends RetrySender<IEvent> {
    private EventManager() {
        super(16);
    }

    private static class Instance {
        private static final EventManager mInstance = new EventManager();
    }

    public static EventManager get() {
        return Instance.mInstance;
    }

    @Override
    protected void onSendFail(@NonNull IEvent iEvent, @NonNull List<String> names, @NonNull ITransRecord record) {
        if (iEvent.shouldRetry()) {
            super.onSendFail(iEvent, names, record);
        }
    }
}
