package com.zpf.tool.expand.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.zpf.api.IconText;

import java.io.File;

/**
 * Created by ZPF on 2018/6/25.
 */
public class IconTextView extends TextView implements IconText {
    private Typeface mOriginalTypeface;
    private Typeface mTypeface;
    private int imageHeight;
    private final String defaultTtfFilePath = "iconfont/iconfont.ttf";//默认的ttf文件位置
    private boolean autoCheck = true;

    public IconTextView(Context context) {
        super(context);
        initOriginalParams(context, null);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initOriginalParams(context, attrs);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initOriginalParams(context, attrs);
    }

    private void initOriginalParams(Context context, @Nullable AttributeSet attrs) {
        mOriginalTypeface = getTypeface();
        mTypeface = mOriginalTypeface;
    }

    @Override
    public void setImageOnly(int id) {
        Drawable drawable = null;
        try {
            drawable = getContext().getResources().getDrawable(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setImageOnly(drawable);
    }

    @Override
    public void setImageOnly(Drawable drawable) {
        setText(null);
        if (drawable != null && drawable.getIntrinsicHeight() > 0) {
            if (imageHeight > 0) {
                zoom(drawable);
                setCompoundDrawables(drawable, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
    }

    @Override
    public void setImage(@DrawableRes int id, @IntRange(from = 0, to = 3) int location) {
        if (location < 0 || location > 3) {
            return;
        }
        Drawable drawable = null;
        try {
            drawable = getContext().getResources().getDrawable(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setImage(drawable, location);
    }

    @Override
    public void setImage(Drawable drawable, @IntRange(from = 0, to = 3) int location) {
        if (location < 0 || location > 3) {
            return;
        }
        zoom(drawable);
        Drawable[] drawables = getCompoundDrawables();
        drawables[location] = drawable;
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    @Override
    public Drawable[] getDrawables() {
        return getCompoundDrawables();
    }

    @Override
    public void setImageHeight(int dpHeight) {
        imageHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight,
                getResources().getDisplayMetrics());
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public void setIconFont(@StringRes int id) {
        if (mTypeface == mOriginalTypeface) {
            setTypefaceFromAsset(defaultTtfFilePath);
        }
        super.setText(id);
    }

    @Override
    public void setIconFont(CharSequence text) {
        if (mTypeface == mOriginalTypeface) {
            setTypefaceFromAsset(defaultTtfFilePath);
        }
        super.setText(text);
    }

    @Override
    public void setTypefaceFromAsset(String path) {
        mTypeface = null;
        if (!TextUtils.isEmpty(path)) {
            try {
                mTypeface = Typeface.createFromAsset(getContext().getAssets(), path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setTypeface(mTypeface);
    }

    @Override
    public void setTypefaceFromFile(File file) {
        mTypeface = null;
        if (file != null && file.exists()) {
            try {
                mTypeface = Typeface.createFromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setTypeface(mTypeface);
    }

    @Override
    public void resetTypeface() {
        mTypeface = mOriginalTypeface;
        setTypeface(mTypeface);
    }

    @Override
    public Typeface getCurrentTypeface() {
        return mTypeface;
    }

    @Override
    public void setTextSize(int size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    @Override
    public int getTextColor() {
        return super.getCurrentTextColor();
    }

    @Override
    public void setAutoCheck(boolean autoCheck) {
        this.autoCheck = autoCheck;
    }

    @Override
    public void setVisibility(int visibility) {
        autoCheck = false;
        super.setVisibility(visibility);
    }

    @Override
    public View getView() {
        return this;
    }

    /**
     * 缩放
     */
    private void zoom(Drawable drawable) {
        if (drawable != null && drawable.getIntrinsicHeight() > 0) {
            float height = imageHeight;
            if (height <= 0) {
                height = getTextSize();
            }
            if (drawable.getIntrinsicHeight() != height) {
                float b = height / drawable.getIntrinsicHeight();
                drawable.setBounds(0, 0, (int) (b * drawable.getIntrinsicWidth()), (int) height);
            } else {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
        }
    }

    public void checkViewShow() {
        if (!autoCheck) {
            return;
        }
        boolean isEmpty = TextUtils.isEmpty(getText());
        if (!isEmpty) {
            if (getVisibility() != View.VISIBLE) {
                super.setVisibility(View.VISIBLE);
            }
        } else {
            Drawable[] drawables = getCompoundDrawables();
            for (Drawable d : drawables) {
                if (d != null) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                if (getVisibility() != View.GONE) {
                    super.setVisibility(View.GONE);
                }
            } else {
                if (getVisibility() != View.VISIBLE) {
                    super.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
