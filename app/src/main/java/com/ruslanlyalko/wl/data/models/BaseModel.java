package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class BaseModel implements Parcelable {

    String key;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public BaseModel() {}

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(!(o instanceof BaseModel)) return false;
        BaseModel baseModel = (BaseModel) o;
        return Objects.equals(getKey(), baseModel.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.key);}

    protected BaseModel(Parcel in) {this.key = in.readString();}

    public static final Creator<BaseModel> CREATOR = new Creator<BaseModel>() {
        @Override
        public BaseModel createFromParcel(Parcel source) {return new BaseModel(source);}

        @Override
        public BaseModel[] newArray(int size) {return new BaseModel[size];}
    };
}
