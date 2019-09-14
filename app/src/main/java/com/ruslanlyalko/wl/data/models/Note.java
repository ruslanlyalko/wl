package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Note extends BaseModel {

    private String title;
    private Date createdAt;
    private boolean isChecked;

    public Note(final String key) {
        this.key = key;
        title = "";
        createdAt = new Date();
    }

    public Note() {
        title = "";
        createdAt = new Date();
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(final boolean hidden) {
        this.isChecked = hidden;
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
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected Note(Parcel in) {
        super(in);
        this.title = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {return new Note(source);}

        @Override
        public Note[] newArray(int size) {return new Note[size];}
    };
}
