package com.example.den.shoppinglist.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ListProduct {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name_product")
    public String nameProduct;
    @ColumnInfo(name = "picture_link")
    public String pictureLink;

    public ListProduct(String nameProduct, String pictureLink) {
        this.nameProduct = nameProduct;
        this.pictureLink = pictureLink;
    }

    public int getLPId() {
        return id;
    }

    public void setLPId(int id) {
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
}
