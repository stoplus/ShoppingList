package com.example.den.shoppinglist.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ProductForList {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "id_list")
    public int idList;
    @ColumnInfo(name = "id_product")
    public int idProduct;

    public ProductForList(int idList, int idProduct) {
        this.idList = idList;
        this.idProduct = idProduct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdList() {
        return idList;
    }

    public void setIdList(int idList) {
        this.idList = idList;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }
}
