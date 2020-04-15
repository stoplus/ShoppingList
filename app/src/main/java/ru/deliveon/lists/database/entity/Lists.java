package ru.deliveon.lists.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Lists implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "sort_num")
    private int sortNum;
    @ColumnInfo(name = "color")
    private int color;

    public Lists(String name, int color) {
        this.name = name;
        this.color = color;
    }

    protected Lists(Parcel in) {
        id = in.readInt();
        name = in.readString();
        sortNum = in.readInt();
        color = in.readInt();
    }

    public static final Creator<Lists> CREATOR = new Creator<Lists>() {
        @Override
        public Lists createFromParcel(Parcel in) {
            return new Lists(in);
        }

        @Override
        public Lists[] newArray(int size) {
            return new Lists[size];
        }
    };

    public int getColor() { return color; }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSortNum() { return sortNum; }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    public int getListId() {
        return id;
    }

    public void setListId(int id) {
        this.id = id;
    }

    public String getListName() {
        return name;
    }

    public void setListName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(sortNum);
        dest.writeInt(color);
    }
}

