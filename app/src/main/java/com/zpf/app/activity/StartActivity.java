package com.zpf.app.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.zpf.app.R;
import com.zpf.app.layout.WelcomeView;
import com.zpf.appLib.base.BaseActivity;
import com.zpf.appLib.util.LogUtil;

public class StartActivity extends BaseActivity {
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = findViewById(R.id.iv_icon);
        if (savedInstanceState == null) {
            LogUtil.e("onCreate");
            pushActivity(WelcomeView.class);
            Glide.with(this).load("www.afj")
                    .apply(new RequestOptions())
                    .thumbnail(Glide.with(this).load(R.mipmap.ic_launcher))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .error(Glide.with(this).load(R.drawable.bg_gray_border_gray_r02))
                    .into(imageView);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("onNewIntent");
        pushActivity(WelcomeView.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume");
        pushActivity(WelcomeView.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.e("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected boolean isRootActivity() {
        return true;
    }

}
