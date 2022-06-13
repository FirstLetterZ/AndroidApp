package com.zpf.tool.expand.util;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import com.zpf.api.OnDataResultListener;
import com.zpf.api.OnProgressListener;
import com.zpf.tool.global.CentralManager;

/**
 * @author Created by ZPF on 2021/2/19.
 */
public class DownloadUtil {
    private final DownloadManager downloadManager;
    private final Application application;
    private volatile long downloadId;
    private final long[] loadState = new long[3];
    private long lastDown = 0;
    private BroadcastReceiver receiver;
    private DownloadListener downloadListener;
    private Thread checkThread;
    private final Object lock = new Object();
    private final Runnable stateCallback = new Runnable() {
        @Override
        public void run() {
            checkStateChange();
        }
    };

    public DownloadUtil(Context context) {
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        application = (Application) context.getApplicationContext();
    }

    public void download(String url, String title, String desc, Uri apkUri) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(desc);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedOverRoaming(false);
        request.setDestinationUri(apkUri);
        lastDown = 0;
        loadState[0] = 0;
        loadState[1] = Long.MAX_VALUE;
        loadState[2] = DownloadManager.STATUS_PENDING;
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId) {
                        onComplete();
                    }
                }
            };
        }
        application.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadId = downloadManager.enqueue(request);
        startQueryState();
    }

    private void startQueryState() {
        if (checkThread == null || checkThread.isInterrupted()) {
            checkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            synchronized (lock) {
                                queryState();
                                lock.wait(800);
                            }
                        }
                    } catch (Exception e) {
                        //
                    } finally {
                        checkThread = null;
                    }
                    queryState();

                }
            });
            checkThread.start();
        }
    }

    private void queryState() {
        Cursor c = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        if (c != null && c.moveToFirst()) {
            int column = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            if (column > 0) {
                loadState[0] = c.getLong(column);
            }
            column = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            if (column > 0) {
                loadState[1] = c.getLong(column);
            }
            column = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (column > 0) {
                loadState[2] = c.getInt(column);
            }
            CentralManager.runOnMainTread(stateCallback);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }

    private void checkStateChange() {
        if (downloadListener == null) {
            return;
        }
        long current = loadState[0];
        long total = loadState[1];
        int status = (int) loadState[2];
        switch (status) {
            case DownloadManager.STATUS_SUCCESSFUL: {
                onComplete();
            }
            case DownloadManager.STATUS_FAILED: {
                onComplete();
            }
            default: {
                if (current != lastDown) {
                    lastDown = current;
                    downloadListener.onProgress(total, current, downloadId);
                }
            }
        }
    }

    private void onComplete() {
        if (downloadId > 0) {
            downloadId = 0;
            if (checkThread != null) {
                checkThread.interrupt();
            }
            if (receiver != null) {
                application.unregisterReceiver(receiver);
                receiver = null;
            }
            if (downloadListener != null) {
                Uri apkUri = downloadManager.getUriForDownloadedFile(downloadId);
                downloadListener.onResult(apkUri != null, apkUri);
            }
        }
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public interface DownloadListener extends OnProgressListener, OnDataResultListener<Uri> {

    }
}
