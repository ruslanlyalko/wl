package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserPush extends BaseModel {

    private String title;
    private String body;
    private Date createdAt;

    public UserPush() {
        createdAt = new Date();
    }

    public UserPush(final String title, final String body) {
        this.title = title;
        this.body = body;
        this.createdAt = new Date();
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        UserPush userPush = (UserPush) o;
        return Objects.equals(title, userPush.title) &&
                Objects.equals(body, userPush.body) &&
                Objects.equals(createdAt, userPush.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, body, createdAt);
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected UserPush(Parcel in) {
        super(in);
        this.title = in.readString();
        this.body = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Parcelable.Creator<UserPush> CREATOR = new Parcelable.Creator<UserPush>() {
        @Override
        public UserPush createFromParcel(Parcel source) {return new UserPush(source);}

        @Override
        public UserPush[] newArray(int size) {return new UserPush[size];}
    };
}
