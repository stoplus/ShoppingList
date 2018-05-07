package com.example.den.shoppinglist.interfaces;


import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.entity.ProductForList;

import java.util.List;

public interface DatabaseCallbackLists {

    void onListsLoaded(List<Lists> lists);

    void onListDeleted();

    void onListsAdded();

    void onDataNotAvailable();

    void onListsUpdated();

    void onSameIdList(List<ProductForList> list);

    void onDeletedListProductForList();

    void onInOtherLists(List<ProductForList> list);

    void onDeletedProductList();
}
