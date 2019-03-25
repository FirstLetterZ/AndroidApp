package com.zpf.app;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ZPF on 2018/11/27.
 */

public class TestAdapter extends ListAdapter<String,TestViewHolder> {

    protected TestAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback) {
        super(diffCallback);
    }

    protected TestAdapter(@NonNull AsyncDifferConfig<String> config) {
        super(config);
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (50 * parent.getResources().getDisplayMetrics().density)));
        return new TestViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        ((TextView) holder.itemView).setText(getItem(position));
    }

}
