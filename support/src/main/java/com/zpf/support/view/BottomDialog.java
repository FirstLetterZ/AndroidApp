package com.zpf.support.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.zpf.support.base.SafeDialog;
import com.zpf.support.base.R;
import com.zpf.api.OnItemClickListener;
import com.zpf.tool.SafeClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/6/18.
 */
public class BottomDialog extends SafeDialog {
    private TextView tvBottom;
    private LinearLayout rootView;
    private List<MenuItemInfo> menuList;
    private OnItemClickListener listener;
    private int listTextColor;
    private RecyclerView.Adapter menuAdapter;
    private float density;
    private int rootViewHeight = -1;

    public BottomDialog(@NonNull Context context) {
        super(context);
    }

    public BottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BottomDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void initView() {
        density = getContext().getResources().getDisplayMetrics().density;
        menuList = new ArrayList<>();
        rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        rootView.setPadding((int) (10 * density), 0, (int) (10 * density), (int) (10 * density));
        Space space = new Space(getContext());
        space.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (10 * density)));
        GradientDrawable rvBg = new GradientDrawable();
        rvBg.setCornerRadius((int) (10 * density));
        rvBg.setColor(Color.WHITE);
        RecyclerView rvContent = new RecyclerView(getContext());
        rvContent.setBackground(rvBg);
        rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        menuAdapter = new BottomDialogListAdapter();
        rvContent.setAdapter(menuAdapter);
        tvBottom = new TextView(getContext());
        tvBottom.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvBottom.setText("取消");
        tvBottom.setGravity(Gravity.CENTER);
        tvBottom.setTextColor(Color.RED);
        tvBottom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (44 * density)));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setCornerRadius((int) (10 * density));
        btnBg.setColor(Color.WHITE);
        tvBottom.setBackground(btnBg);
        tvBottom.setOnClickListener(new SafeClickListener() {
            @Override
            public void click(View v) {
                dismiss();
            }
        });
        rootView.addView(rvContent);
        rootView.addView(space);
        rootView.addView(tvBottom);
        setContentView(rootView);
    }

    @Override
    protected void initWindow(@NonNull Window window) {
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.bottom_dialog); // 设置显示动画
    }

    public int getListTextColor() {
        return listTextColor;
    }

    public void setListTextColor(@ColorInt int listTextColor) {
        this.listTextColor = listTextColor;
    }

    public TextView getTvBottom() {
        return tvBottom;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public List<MenuItemInfo> getMenuList() {
        return menuList;
    }

    public <T extends MenuItemInfo> void setMenuList(List<T> menuList) {
        this.menuList.clear();
        if (menuList != null && menuList.size() > 0) {
            this.menuList.addAll(menuList);
        }
        setViewHeight();
        if (isShowing()) {
            menuAdapter.notifyDataSetChanged();
        }
    }

    public void setStringMenuList(List<String> menuList) {
        this.menuList.clear();
        if (menuList != null && menuList.size() > 0) {
            for (String menu : menuList) {
                this.menuList.add(new BaseMenuItemInfo(menu));
            }
        }
        setViewHeight();
        if (isShowing()) {
            menuAdapter.notifyDataSetChanged();
        }
    }

    public void setStringMenuList(String[] menuList) {
        this.menuList.clear();
        if (menuList != null && menuList.length > 0) {
            for (String menu : menuList) {
                this.menuList.add(new BaseMenuItemInfo(menu));
            }
        }
        setViewHeight();
        if (isShowing()) {
            menuAdapter.notifyDataSetChanged();
        }
    }

    private void setViewHeight() {
        float size = menuList.size();
        if (size > 6) {
            size = 5.5f;
        }
        int height = (int) ((43 * size + 44 + 10 + 10) * density);
        if (rootViewHeight != height) {
            rootViewHeight = height;
            rootView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
        }
    }

    private class BottomDialogListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView item = new TextView(parent.getContext());
            item.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (42 * density)));
            item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            item.setGravity(Gravity.CENTER);
            View line = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (1 * density));
            params.setMargins((int) (5 * density), 0, (int) (5 * density), 0);
            line.setLayoutParams(params);
            line.setBackgroundColor(Color.parseColor("#eeeeee"));
            layout.addView(item);
            layout.addView(line);
            return new MyViewHolder(layout, item, line);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tvItem.setText(menuList.get(position).getMenuItemDisplay());
            if (listTextColor != 0) {
                try {
                    holder.tvItem.setTextColor(listTextColor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            final int n = position;
            holder.tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null) {
                        listener.onItemClick(n);
                    }
                }
            });
            if (position == menuList.size() - 1) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return menuList == null ? 0 : menuList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        View line;

        MyViewHolder(View itemView, TextView tvItem, View line) {
            super(itemView);
            this.tvItem = tvItem;
            this.line = line;
        }
    }

    public interface MenuItemInfo {
        String getMenuItemDisplay();
    }

    public static class BaseMenuItemInfo implements MenuItemInfo {
        private String display;

        public BaseMenuItemInfo(String display) {
            this.display = display;
        }

        @Override
        public String getMenuItemDisplay() {
            return display;
        }
    }
}
