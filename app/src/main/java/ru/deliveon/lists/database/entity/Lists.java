package ru.deliveon.lists.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Lists implements Serializable {
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
}

