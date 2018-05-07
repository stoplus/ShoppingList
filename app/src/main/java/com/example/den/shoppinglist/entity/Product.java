package com.example.den.shoppinglist.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Product implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name_product")
    public String nameProduct;
    @ColumnInfo(name = "picture_link")
    public String pictureLink;
    @ColumnInfo(name = "check_box")
    public boolean check_box;
    @ColumnInfo(name = "bought")
    public boolean bought;
    @ColumnInfo(name = "id_paren_list")
    public int idParenList;


    public Product(String nameProduct, String pictureLink, boolean bought) {
        this.nameProduct = nameProduct;
        this.pictureLink = pictureLink;
        this.bought = bought;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        nameProduct = in.readString();
        pictureLink = in.readString();
        check_box = in.readByte() != 0;
        bought = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public boolean isCheck_box() {
        return check_box;
    }

    public void setCheck_box(boolean check_box) {
        this.check_box = check_box;
    }

    public int getProductId() {
        return id;
    }

    public void setProductId(int id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nameProduct);
        dest.writeString(pictureLink);
        dest.writeByte((byte) (check_box ? 1 : 0));
        dest.writeByte((byte) (bought ? 1 : 0));
    }
}
