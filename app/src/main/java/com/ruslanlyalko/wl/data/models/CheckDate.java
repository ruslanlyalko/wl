package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class CheckDate implements Parcelable {

    private boolean isRight;

    public CheckDate() {
    }

    public CheckDate(Object object) {
        if(object instanceof HashMap) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            isRight = (Boolean) hashMap.get("isRight");
        }
    }

    public boolean getIsRight() {
        return isRight;
    }

    public void setIsRight(final boolean isBlocked) {
        this.isRight = isBlocked;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeByte(this.isRight ? (byte) 1 : (byte) 0);}

    protected CheckDate(Parcel in) {this.isRight = in.readByte() != 0;}

    public static final Creator<CheckDate> CREATOR = new Creator<CheckDate>() {
        @Override
        public CheckDate createFromParcel(Parcel source) {return new CheckDate(source);}

        @Override
        public CheckDate[] newArray(int size) {return new CheckDate[size];}
    };
}
