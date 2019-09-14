package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class CheckBlocked implements Parcelable {

    private boolean isBlocked;

    public CheckBlocked() {
    }

    public CheckBlocked(Object object) {
        if(object instanceof HashMap) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            isBlocked = (Boolean) hashMap.get("isBlocked");
        }
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(final boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeByte(this.isBlocked ? (byte) 1 : (byte) 0);}

    protected CheckBlocked(Parcel in) {this.isBlocked = in.readByte() != 0;}

    public static final Creator<CheckBlocked> CREATOR = new Creator<CheckBlocked>() {
        @Override
        public CheckBlocked createFromParcel(Parcel source) {return new CheckBlocked(source);}

        @Override
        public CheckBlocked[] newArray(int size) {return new CheckBlocked[size];}
    };
}
