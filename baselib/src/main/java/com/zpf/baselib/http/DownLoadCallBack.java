package com.zpf.baselib.http;

import com.zpf.baselib.util.AppContext;
import com.zpf.baselib.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by ZPF on 2018/3/6.
 */
public abstract class DownLoadCallBack extends BaseCallBack<ResponseBody> {
    private File file;
    private OnSaveListener listener;
    private boolean saveResult = false;

    public DownLoadCallBack(OnSaveListener listener) {
        this.listener = listener;
    }

    public DownLoadCallBack(OnSaveListener listener, int type) {
        super(type);
        this.listener = listener;
    }

    public DownLoadCallBack(File file) {
        super();
        this.file = file;
    }

    public DownLoadCallBack(File file, int type) {
        super(type);
        this.file = file;
    }

    @Override
    public void onNext(ResponseBody result) {
        if (saveResult) {
            String filePath = getFilePath();
            if (filePath != null && (filePath.endsWith(".jpg") || filePath.endsWith(".png"))) {
                FileUtil.notifyPhotoAlbum(AppContext.get(), filePath);
            }
        }
        complete(saveResult);
    }

    @Override
    protected void next(ResponseBody responseBody) {
    }

    //不在主线程
    public boolean checkFileSuccess(File file) {
        return file != null && file.exists();
    }

    public void saveFile(ResponseBody body) {
        if (listener != null) {
            file = listener.save(body);
            saveResult = checkFileSuccess(file);
        } else {
            if (file != null) {
                String filePath = file.getAbsolutePath();
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                    file = new File(filePath);
                }
                writeToFile(body);
            } else {
                file = null;
            }
            saveResult = checkFileSuccess(file);
        }
    }

    private void writeToFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnSaveListener {
        File save(ResponseBody body);
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        if (file != null && file.exists()) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }
}
