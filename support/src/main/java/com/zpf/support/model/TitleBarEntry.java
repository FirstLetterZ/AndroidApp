package com.zpf.support.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorInt;

public class TitleBarEntry implements Parcelable {
    public IconTextEntry leftIconEntry;
    public IconTextEntry leftTextEntry;
    public IconTextEntry titleEntry;
    public IconTextEntry subtitleEntry;
    public IconTextEntry rightTextEntry;
    public IconTextEntry rightIconEntry;
    public String leftLayoutAction;
    public String rightLayoutAction;
    public boolean hideStatusBar;
    public boolean hideTitleBar;
    @ColorInt
    public int statusBarColor;
    @ColorInt
    public int titleBarColor;

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
        hideStatusBar = in.readByte() != 0;
        hideTitleBar = in.readByte() != 0;
        statusBarColor = in.readInt();
        titleBarColor = in.readInt();
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
        dest.writeByte((byte) (hideStatusBar ? 1 : 0));
        dest.writeByte((byte) (hideTitleBar ? 1 : 0));
        dest.writeInt(statusBarColor);
        dest.writeInt(titleBarColor);
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
}
