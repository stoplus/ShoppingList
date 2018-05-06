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
    @ColumnInfo(name = "check_box")
    public boolean check_box;

    public ListProduct(String nameProduct, String pictureLink) {
        this.nameProduct = nameProduct;
        this.pictureLink = pictureLink;
        this.pictureLink = pictureLink;
    }

    public boolean isCheck_box() {
        return check_box;
    }

    public void setCheck_box(boolean check_box) {
        this.check_box = check_box;
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
