package com.zpf.support.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zpf.api.OnItemClickListener;
import com.zpf.file.FileUriUtil;
import com.zpf.file.FileUtil;
import com.zpf.frame.IViewContainer;
import com.zpf.support.R;
import com.zpf.support.util.PhotoUtil;
import com.zpf.tool.PublicUtil;
import com.zpf.tool.expand.cache.SpUtil;

import java.io.File;

/**
 * Created by ZPF on 2018/6/22.
 */
public class SelectPhotoDialog extends BottomMenuDialog {
    public static int BOTH = 0;
    public static final String PHOTO_PATH = "photo_path";
    private int type = BOTH;
    public static final int REQ_CAMERA = 6661;
    public static final int REQ_ALBUM = 6662;
    private IViewContainer viewContainer;
    private Activity activity;
    private android.app.Fragment fragment;
    private androidx.fragment.app.Fragment compatFragment;

    public SelectPhotoDialog(@NonNull IViewContainer viewContainer) {
        super(viewContainer.getCurrentActivity());
        this.viewContainer = viewContainer;
    }

    public SelectPhotoDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public SelectPhotoDialog(@NonNull android.app.Fragment fragment) {
        super(fragment.getContext());
        this.fragment = fragment;
    }

    public SelectPhotoDialog(@NonNull androidx.fragment.app.Fragment fragment) {
        super(fragment.getContext());
        this.compatFragment = fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        getTvBottom().setTextColor(Color.RED);
        getTvBottom().setText(R.string.support_button_cancel);
        setStringMenuList(new String[]{PublicUtil.getString(R.string.support_option_take_photo),
                PublicUtil.getString(R.string.support_option_photo_album)});
        setListTextColor(Color.BLACK);
        setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, @Nullable View view) {
                switch (position) {
                    case 0://拍照
                        takePhoto();
                        break;
                    case 1://相册
                        selectAlbum();
                        break;
                }
                dismiss();
            }
        });
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void show() {
        if (type == 0) {
            super.show();
        } else if (type < 0) {//拍照
            takePhoto();
        } else {//相册
            selectAlbum();
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private void takePhoto() {
        String name = "photoCache" + System.currentTimeMillis() + ".jpg";
        File photoFile = FileUtil.getFileOrCreate(
                getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "CameraCache", name);
        SpUtil.put(PHOTO_PATH, photoFile.getAbsolutePath());
        if (viewContainer != null) {
            PhotoUtil.takePhoto(viewContainer, photoFile, REQ_CAMERA);
        } else if (activity != null) {
            PhotoUtil.takePhoto(activity, photoFile, REQ_CAMERA);
        } else if (fragment != null) {
            PhotoUtil.takePhoto(fragment, photoFile, REQ_CAMERA);
        } else if (compatFragment != null) {
            PhotoUtil.takePhoto(compatFragment, photoFile, REQ_CAMERA);
        }
    }

    private void selectAlbum() {
        if (viewContainer != null) {
            PhotoUtil.selectFromAlbum(viewContainer, REQ_ALBUM);
        } else if (activity != null) {
            PhotoUtil.selectFromAlbum(activity, REQ_ALBUM);
        } else if (fragment != null) {
            PhotoUtil.selectFromAlbum(fragment, REQ_ALBUM);
        } else if (compatFragment != null) {
            PhotoUtil.selectFromAlbum(compatFragment, REQ_ALBUM);
        }
    }

    public String getCameraPhotoPath() {
        return SpUtil.getString(PHOTO_PATH);
    }

    public String getAlbumPhotoPath(Intent data) {
        String path = null;
        if (data != null) {
            Uri uriAlbum = data.getData();
            path = FileUriUtil.uriToPath(getContext(), uriAlbum);
        }
        if (TextUtils.isEmpty(path)) {
            path = "";
        }
        return path;
    }

}
