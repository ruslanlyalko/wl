package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserStatistic extends BaseModel {

    private String id;
    private String name;
    private String department;
    private double time;

    public UserStatistic() {
    }

    public UserStatistic(final String id, final String name, final String department, final double time) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.time = time;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(final String department) {
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getTime() {
        return time;
    }

    public void setTime(final double time) {
        this.time = time;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.department);
        dest.writeDouble(this.time);
    }

    protected UserStatistic(Parcel in) {
        super(in);
        this.id = in.readString();
        this.name = in.readString();
        this.department = in.readString();
        this.time = in.readDouble();
    }

    public static final Creator<UserStatistic> CREATOR = new Creator<UserStatistic>() {
        @Override
        public UserStatistic createFromParcel(Parcel source) {return new UserStatistic(source);}

        @Override
        public UserStatistic[] newArray(int size) {return new UserStatistic[size];}
    };
}
