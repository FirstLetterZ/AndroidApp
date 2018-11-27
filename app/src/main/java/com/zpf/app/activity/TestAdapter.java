package com.zpf.app.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ZPF on 2018/11/27.
 */

public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {
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
        ((TextView) holder.itemView).setText(("第" + (position + 1) + "条"));
    }

    @Override
    public int getItemCount() {
        return 14;
    }
}
