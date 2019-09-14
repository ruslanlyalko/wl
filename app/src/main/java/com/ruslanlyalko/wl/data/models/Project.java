package com.ruslanlyalko.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Project extends BaseModel implements Parcelable {

    private String title;
    private boolean isHidden;
    private Date createdAt = new Date();
    private List<Note> notes = new ArrayList<>();

    public Project() {
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(final List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(!(o instanceof Project)) return false;
        if(!super.equals(o)) return false;
        Project project = (Project) o;
        return isHidden == project.isHidden &&
                Objects.equals(getTitle(), project.getTitle()) &&
                Objects.equals(getCreatedAt(), project.getCreatedAt()) &&
                Objects.equals(getNotes(), project.getNotes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle(), getCreatedAt(), isHidden, getNotes());
    }

    public boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(final boolean hidden) {
        this.isHidden = hidden;
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
        dest.writeByte(this.isHidden ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeTypedList(this.notes);
    }

    protected Project(Parcel in) {
        super(in);
        this.title = in.readString();
        this.isHidden = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.notes = in.createTypedArrayList(Note.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {return new Project(source);}

        @Override
        public Project[] newArray(int size) {return new Project[size];}
    };
}
