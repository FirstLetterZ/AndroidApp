package com.zpf.support.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zpf.support.R;

public class TriangleView extends View {
    public interface TriangleDirection {
        int LEFT = 0;
        int TOP = 1;
        int RIGHT = 2;
        int BOTTOM = 3;
    }

    private Path path;
    private Paint paint;
    private int direction = TriangleDirection.TOP;

    public TriangleView(Context context) {
        this(context, null, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TriangleView);
        if (typedArray != null) {
            initValue(context, typedArray);
        }
    }

    private void initValue(Context context, TypedArray typedArray) {
        int color = typedArray.getColor(R.styleable.TriangleView_triangle_color, Color.BLACK);
        int d = typedArray.getColor(R.styleable.TriangleView_triangle_direction, TriangleDirection.TOP);
        setTriangleColor(color);
        setTriangleDirection(d);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        if (direction == TriangleDirection.LEFT) {//向左
            path.moveTo(0, getHeight() / 2);
            path.lineTo(getWidth(), getHeight());
            path.lineTo(getWidth(), 0);
            path.close();
        } else if (direction == TriangleDirection.TOP) {//向上
            path.moveTo(getWidth() / 2, 0);
            path.lineTo(getWidth(), getHeight());
            path.lineTo(0, getHeight());
            path.close();
        } else if (direction == TriangleDirection.RIGHT) {//向右
            path.moveTo(0, 0);
            path.lineTo(0, getHeight());
            path.lineTo(getWidth(), getHeight() / 2);
            path.close();
        } else if (direction == TriangleDirection.BOTTOM) {//向下
            path.moveTo(0, 0);
            path.lineTo(getWidth(), 0);
            path.lineTo(getWidth() / 2, getHeight());
            path.close();
        }
        canvas.drawPath(path, paint);
    }

    public void setPaint(@NonNull Paint newPaint) {
        paint = newPaint;
    }

    public void setTriangleColor(@ColorInt int color) {
        paint.setColor(color);
    }

    public void setTriangleDirection(int d) {
        direction = Math.abs(d % 4);
    }

    public int getTriangleDirection() {
        return direction;
    }

}