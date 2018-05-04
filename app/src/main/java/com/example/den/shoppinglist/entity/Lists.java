package com.example.den.shoppinglist.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Lists {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;

    public Lists(String name) {
        this.name = name;
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

