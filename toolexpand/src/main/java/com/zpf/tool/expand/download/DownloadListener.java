package com.zpf.tool.expand.download;

import android.net.Uri;

import com.zpf.api.OnDataResultListener;
import com.zpf.api.OnProgressListener;

public interface DownloadListener extends OnProgressListener, OnDataResultListener<Uri> {
}