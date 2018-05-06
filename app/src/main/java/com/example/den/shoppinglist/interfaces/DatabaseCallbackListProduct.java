package com.example.den.shoppinglist.interfaces;

import com.example.den.shoppinglist.entity.ListProduct;

import java.util.List;

public interface DatabaseCallbackListProduct {
    void onListsLoaded(List<ListProduct> lists);

    void onListsDeleted();

    void onListsAdded();

    void onDataNotAvailable();

    void onListsUpdated();
}
