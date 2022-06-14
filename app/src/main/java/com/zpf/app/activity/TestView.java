package com.zpf.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.DataBinderMapperImpl;

import com.zpf.app.R;
import com.zpf.support.base.ViewProcessor;

/**
 * Created by ZPF on 2019/3/25.
 */
public class TestView extends ViewProcessor {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT);
        Log.e("ZPF", "onResume==>>");
    }

    /**
     * @param targetView
     * @param resId
     * @param res
     */
    public void setBgDrawable(View targetView, int resId, Resources res) {
        //测量图片实际大小
        BitmapFactory.Options measureOpts = new BitmapFactory.Options();
        measureOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, measureOpts);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = res.getDisplayMetrics().densityDpi;
        opts.inScaled = true;
        //计算缩放
        opts.inTargetDensity = targetView.getWidth() * opts.inDensity / measureOpts.outWidth;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, opts);
        Log.e("ZPF", "bitmap.width=" + bitmap.getWidth());
        BitmapDrawable bgDrawable = new BitmapDrawable(res, bitmap);
        //设置填充模式，由于X轴充满视图，所以TileMode可以为null
        bgDrawable.mutate();
        bgDrawable.setTileModeXY(null, Shader.TileMode.CLAMP);
        bgDrawable.setDither(true);
        Log.e("ZPF", "bgDrawable.width=" + bgDrawable.getIntrinsicWidth());
        targetView.setBackground(bgDrawable);
    }

}
