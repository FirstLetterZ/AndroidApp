package com.zpf.support.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

public class TitleBarEntry implements Parcelable {

    private IconTextEntry leftIconEntry;
    private IconTextEntry leftTextEntry;
    private IconTextEntry titleEntry;
    private IconTextEntry subtitleEntry;
    private IconTextEntry rightTextEntry;
    private IconTextEntry rightIconEntry;
    private String leftLayoutAction;
    private String rightLayoutAction;
    private boolean showStatusBar;
    private boolean showBottomShadow;
    private boolean showTitleBar;
    @ColorInt
    private int backColor;

    public TitleBarEntry() {
    }

    protected TitleBarEntry(Parcel in) {
        leftIconEntry = in.readParcelable(IconTextEntry.class.getClassLoader());
        leftTextEntry = in.readParcelable(IconTextEntry.class.getClassLoader());
        titleEntry = in.readParcelable(IconTextEntry.class.getClassLoader());
        subtitleEntry = in.readParcelable(IconTextEntry.class.getClassLoader());
        rightTextEntry = in.readParcelable(IconTextEntry.class.getClassLoader());
        rightIconEntry = in.readParcelable(IconTextEntry.class.getClassLoader());
        leftLayoutAction = in.readString();
        rightLayoutAction = in.readString();
        showStatusBar = in.readByte() != 0;
        showBottomShadow = in.readByte() != 0;
        showTitleBar = in.readByte() != 0;
        backColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(leftIconEntry, flags);
        dest.writeParcelable(leftTextEntry, flags);
        dest.writeParcelable(titleEntry, flags);
        dest.writeParcelable(subtitleEntry, flags);
        dest.writeParcelable(rightTextEntry, flags);
        dest.writeParcelable(rightIconEntry, flags);
        dest.writeString(leftLayoutAction);
        dest.writeString(rightLayoutAction);
        dest.writeByte((byte) (showStatusBar ? 1 : 0));
        dest.writeByte((byte) (showBottomShadow ? 1 : 0));
        dest.writeByte((byte) (showTitleBar ? 1 : 0));
        dest.writeInt(backColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TitleBarEntry> CREATOR = new Creator<TitleBarEntry>() {
        @Override
        public TitleBarEntry createFromParcel(Parcel in) {
            return new TitleBarEntry(in);
        }

        @Override
        public TitleBarEntry[] newArray(int size) {
            return new TitleBarEntry[size];
        }
    };

    public IconTextEntry getLeftIconEntry() {
        return leftIconEntry;
    }

    public void setLeftIconEntry(IconTextEntry leftIconEntry) {
        this.leftIconEntry = leftIconEntry;
    }

    public IconTextEntry getLeftTextEntry() {
        return leftTextEntry;
    }

    public void setLeftTextEntry(IconTextEntry leftTextEntry) {
        this.leftTextEntry = leftTextEntry;
    }

    public IconTextEntry getTitleEntry() {
        return titleEntry;
    }

    public void setTitleEntry(IconTextEntry titleEntry) {
        this.titleEntry = titleEntry;
    }

    public IconTextEntry getSubtitleEntry() {
        return subtitleEntry;
    }

    public void setSubtitleEntry(IconTextEntry subtitleEntry) {
        this.subtitleEntry = subtitleEntry;
    }

    public IconTextEntry getRightTextEntry() {
        return rightTextEntry;
    }

    public void setRightTextEntry(IconTextEntry rightTextEntry) {
        this.rightTextEntry = rightTextEntry;
    }

    public IconTextEntry getRightIconEntry() {
        return rightIconEntry;
    }

    public void setRightIconEntry(IconTextEntry rightIconEntry) {
        this.rightIconEntry = rightIconEntry;
    }

    public String getLeftLayoutAction() {
        return leftLayoutAction;
    }

    public void setLeftLayoutAction(String leftLayoutAction) {
        this.leftLayoutAction = leftLayoutAction;
    }

    public String getRightLayoutAction() {
        return rightLayoutAction;
    }

    public void setRightLayoutAction(String rightLayoutAction) {
        this.rightLayoutAction = rightLayoutAction;
    }

    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }

    public boolean isShowBottomShadow() {
        return showBottomShadow;
    }

    public void setShowBottomShadow(boolean showBottomShadow) {
        this.showBottomShadow = showBottomShadow;
    }

    public boolean isShowTitleBar() {
        return showTitleBar;
    }

    public void setShowTitleBar(boolean showTitleBar) {
        this.showTitleBar = showTitleBar;
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }
}
