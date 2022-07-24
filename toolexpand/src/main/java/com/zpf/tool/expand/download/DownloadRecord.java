package com.zpf.tool.expand.download;

import android.app.DownloadManager;

class DownloadRecord {
    final long downloadId;
    long current = 0;
    long total = Integer.MAX_VALUE;
    int status = DownloadManager.STATUS_PENDING;

    private long currentOld = 0;
    private int statusOld = DownloadManager.STATUS_PENDING;

    DownloadListener downloadListener;

    DownloadRecord(long downloadId) {
        this.downloadId = downloadId;
    }

    public void noticeProgressOrFail() {
        final DownloadListener listener = downloadListener;
        if (listener != null) {
            if (statusOld != status && status == DownloadManager.STATUS_FAILED) {
                listener.onResult(false, null);
            } else if (current > 0 && currentOld != current) {
                listener.onProgress(total, current, downloadId);
            }
        }
        currentOld = current;
        statusOld = status;
    }

}