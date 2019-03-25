package com.zpf.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZPF on 2019/1/12.
 */

public class TestBaen implements Parcelable{
    private String a = "";
    private boolean b = false;
    private char c = '1';
    private byte d = 'b';

    public TestBaen() {
    }

    protected TestBaen(Parcel in) {
        a = in.readString();
        b = in.readByte() != 0;
        c = (char) in.readInt();
        d = in.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(a);
        dest.writeByte((byte) (b ? 1 : 0));
        dest.writeInt((int) c);
        dest.writeByte(d);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TestBaen> CREATOR = new Creator<TestBaen>() {
        @Override
        public TestBaen createFromParcel(Parcel in) {
            return new TestBaen(in);
        }

        @Override
        public TestBaen[] newArray(int size) {
            return new TestBaen[size];
        }
    };
}
