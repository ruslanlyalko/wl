package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ADate implements Parcelable {
    private Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(final Long time) {
        this.time = time;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeValue(this.time);}

    public ADate() {
        time = new Date().getTime();
    }

    protected ADate(Parcel in) {this.time = (Long) in.readValue(Long.class.getClassLoader());}

    public static final Creator<ADate> CREATOR = new Creator<ADate>() {
        @Override
        public ADate createFromParcel(Parcel source) {return new ADate(source);}

        @Override
        public ADate[] newArray(int size) {return new ADate[size];}
    };
}
