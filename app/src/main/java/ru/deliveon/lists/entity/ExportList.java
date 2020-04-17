package ru.deliveon.lists.entity;

import java.io.Serializable;
import java.util.List;

import ru.deliveon.lists.database.entity.Product;

public class ExportList implements Serializable {
    public String name;
    private int color;
    private List<Product> listProduct;

    public ExportList(String name, int color, List<Product> listProduct) {
        this.name = name;
        this.color = color;
        this.listProduct = listProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(List<Product> listProduct) {
        this.listProduct = listProduct;
    }
}
