package com.example.den.shoppinglist.interfaces;

import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.entity.ProductForList;

import java.util.List;

public interface DatabaseCallbackListProduct {
    void onListProductsLoaded(List<Product> list);

    void onProductDeleted();

    void onProductForListDeleted();

    void onProductAdded();

    void onProductForListAdded();

    void onDataNotAvailable();

    void onProductUpdated();

    void onLastProduct(int id);

    void onSameIdProductForList(List<ProductForList> list);

}
