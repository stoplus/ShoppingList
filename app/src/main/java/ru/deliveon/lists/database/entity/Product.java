package ru.deliveon.lists.database.entity;

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
    @ColumnInfo(name = "bought")
    public boolean bought;
//    @ColumnInfo(name = "sort_num")
//    public int sortNum;


    public Product(String nameProduct, String pictureLink, boolean bought) {
//    public Product(String nameProduct, String pictureLink, boolean bought, int sortNum) {
        this.nameProduct = nameProduct;
        this.pictureLink = pictureLink;
        this.bought = bought;
//        this.sortNum = sortNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        nameProduct = in.readString();
        pictureLink = in.readString();
        bought = in.readByte() != 0;
//        sortNum = in.readInt();
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

//    public int getSortNum() {
//        return sortNum;
//    }
//
//    public void setSortNum(int sortNum) {
//        this.sortNum = sortNum;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nameProduct);
        dest.writeString(pictureLink);
        dest.writeByte((byte) (bought ? 1 : 0));
//        dest.writeInt(sortNum);
    }
}
