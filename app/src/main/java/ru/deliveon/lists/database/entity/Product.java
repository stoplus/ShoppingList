package ru.deliveon.lists.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Product implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name_product")
    public String nameProduct;
    @ColumnInfo(name = "picture_link")
    public String pictureLink;
    @ColumnInfo(name = "bought")
    public boolean bought;
    @ColumnInfo(name = "sort_num")
    public int sortNum;

    public Product(String nameProduct, String pictureLink, boolean bought, int sortNum) {
        this.nameProduct = nameProduct;
        this.pictureLink = pictureLink;
        this.bought = bought;
        this.sortNum = sortNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }
}
