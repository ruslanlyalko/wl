package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class AppSettings implements Parcelable {

    private String androidLatestVersion;
    private boolean isSnowing;

    public AppSettings() {}


    public String getAndroidLatestVersion() {
        return androidLatestVersion;
    }

    public void setAndroidLatestVersion(final String androidLatestVersion) {
        this.androidLatestVersion = androidLatestVersion;
    }

    public boolean getIsSnowing() {
        return isSnowing;
    }

    public void setIsSnowing(final boolean snowig) {
        isSnowing = snowig;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AppSettings)) return false;
        AppSettings that = (AppSettings) o;
        return isSnowing == that.isSnowing &&
                Objects.equals(getAndroidLatestVersion(), that.getAndroidLatestVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAndroidLatestVersion(), isSnowing);
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("isSnowing", isSnowing);
        return result;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.androidLatestVersion);
        dest.writeByte(this.isSnowing ? (byte) 1 : (byte) 0);
    }

    protected AppSettings(Parcel in) {
        this.androidLatestVersion = in.readString();
        this.isSnowing = in.readByte() != 0;
    }

    public static final Creator<AppSettings> CREATOR = new Creator<AppSettings>() {
        @Override
        public AppSettings createFromParcel(Parcel source) {return new AppSettings(source);}

        @Override
        public AppSettings[] newArray(int size) {return new AppSettings[size];}
    };
}
