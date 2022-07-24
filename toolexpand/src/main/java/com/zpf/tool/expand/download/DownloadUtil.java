package com.zpf.tool.expand.download;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Created by ZPF on 2021/2/19.
 */
public class DownloadUtil {
    private final ConcurrentHashMap<Long, DownloadRecord> recordMap = new ConcurrentHashMap<>();
    private final List<DownloadRecord> waitRemoveList = new LinkedList<>();
    public final DownloadManager downloadManager;
    private final ContentObserver downloadObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            startQueryState();
        }
    };
    private final String managerPackageName = "com.android.providers.downloads";
    private final Application application;
    private final Object lock = new Object();
    private volatile static DownloadUtil mInstance;
    private volatile Thread checkThread;
    private volatile long lastUpdate;
    private final AtomicInteger loadingCount = new AtomicInteger(0);
    private final long updateInterval = 500;

    private DownloadUtil(Context context) {
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        application = (Application) context.getApplicationContext();
        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadId > 0) {
                        DownloadRecord record = recordMap.get(downloadId);
                        if (record != null) {
                            startQueryState();
                        }
                    }
                }
            }
        };
        application.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static DownloadUtil get(Context context) {
        if (mInstance == null) {
            synchronized (DownloadUtil.class) {
                if (mInstance == null) {
                    mInstance = new DownloadUtil(context);
                }
            }
        }
        return mInstance;
    }

    //检查系统下载是否可用
    public boolean isManagerUsable() {
        int state = application.getPackageManager().getApplicationEnabledSetting(managerPackageName);
        return state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                && state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED;
    }

    public void openDownloadManagerSetting() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
            intent.setData(Uri.parse("package:" + managerPackageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        }
    }

    //需要调用isManagerUsable检查是否可用，否则可能报错
    public long download(@NonNull DownloadManager.Request request, @Nullable DownloadListener downloadListener) {
        long downloadId = downloadManager.enqueue(request);
        DownloadRecord record = recordMap.get(downloadId);
        if (record == null) {
            record = new DownloadRecord(downloadId);
            record.status = DownloadManager.STATUS_PENDING;
            recordMap.put(downloadId, record);
            loadingCount.incrementAndGet();
        }
        record.downloadListener = downloadListener;
        startQueryState();
        return downloadId;
    }

    private void startQueryState() {
        if (checkThread == null || checkThread.isInterrupted()) {
            synchronized (DownloadUtil.class) {
                if (checkThread == null || checkThread.isInterrupted()) {
                    checkThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int lastCount = -1;
                            long nextUpdate;
                            try {
                                while (true) {
                                    queryLoading();
                                    if (lastCount == 0 && loadingCount.get() == 0) {
                                        if (quit(false)) {
                                            break;
                                        }
                                    }
                                    lastUpdate = System.currentTimeMillis();
                                    lastCount = loadingCount.get();
                                    if (lastCount > 0) {
                                        nextUpdate = updateInterval;
                                    } else {
                                        nextUpdate = 4 * updateInterval;
                                    }
                                    synchronized (lock) {
                                        lock.wait(nextUpdate);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                quit(true);
                            }
                        }
                    });
                }
            }
            checkThread.start();
            application.getContentResolver().registerContentObserver(
                    Uri.parse("content://downloads/my_downloads"), true, downloadObserver);
        } else {
            if (System.currentTimeMillis() - lastUpdate >= updateInterval) {
                synchronized (lock) {
                    lock.notify();
                }
            }
        }
    }

    private void queryLoading() {
        loadingCount.set(0);
        waitRemoveList.clear();
        Cursor c = null;
        DownloadListener listener;
        for (DownloadRecord record : recordMap.values()) {
            listener = record.downloadListener;
            if (record.status == DownloadManager.STATUS_SUCCESSFUL) {
                waitRemoveList.add(record);
                continue;
            }
            if (record.current > 0 && record.current == record.total) {
                record.status = DownloadManager.STATUS_SUCCESSFUL;
                waitRemoveList.add(record);
                continue;
            }
            c = downloadManager.query(new DownloadManager.Query().setFilterById(record.downloadId));
            if (c != null && c.moveToFirst()) {
                int column = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                if (column > 0) {
                    record.current = c.getLong(column);
                }
                column = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                if (column > 0) {
                    record.total = c.getLong(column);
                }
                if (record.total <= 0) {
                    record.total = Integer.MAX_VALUE;
                }
                column = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (column > 0) {
                    record.status = c.getInt(column);
                } else {
                    record.status = DownloadManager.STATUS_RUNNING;
                }
                switch (record.status) {
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        Uri apkUri = downloadManager.getUriForDownloadedFile(record.downloadId);
                        if (listener != null) {
                            listener.onResult(true, apkUri);
                        }
                        waitRemoveList.add(record);
                        break;
                    }
                    case DownloadManager.STATUS_RUNNING: {
                        loadingCount.incrementAndGet();
                    }
                    default: {
                        record.noticeProgressOrFail();
                    }
                }
            } else {
                record.status = -1;
                waitRemoveList.add(record);
            }
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        for (DownloadRecord record : waitRemoveList) {
            if (record.status == DownloadManager.STATUS_SUCCESSFUL) {
                recordMap.remove(record.downloadId);
                Uri apkUri = downloadManager.getUriForDownloadedFile(record.downloadId);
                listener = record.downloadListener;
                if (listener != null) {
                    listener.onResult(true, apkUri);
                }
            }
        }
    }

    private boolean quit(boolean force) {
        synchronized (DownloadUtil.class) {
            if (force || loadingCount.get() == 0) {
                checkThread = null;
                application.getContentResolver().unregisterContentObserver(downloadObserver);
                return true;
            }
        }
        return false;
    }
}