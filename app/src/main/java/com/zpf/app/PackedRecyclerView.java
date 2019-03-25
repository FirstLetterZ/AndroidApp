package com.zpf.app;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.zpf.tool.expand.view.PackedLayout;

/**
 * Created by ZPF on 2019/1/28.
 */
public class PackedRecyclerView extends PackedLayout<RecyclerView> {

    public PackedRecyclerView(@NonNull Context context) {
        super(context);
    }

    public PackedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PackedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected RecyclerView createContentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setBackgroundColor(Color.GREEN);
        return recyclerView;
    }

    @Override
    public void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

    }
}
