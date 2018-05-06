package com.example.den.shoppinglist.interfaces;


import com.example.den.shoppinglist.entity.Lists;

import java.util.List;

public interface DatabaseCallbackLists {

    void onListsLoaded(List<Lists> lists);

    void onListsDeleted();

    void onListsAdded();

    void onDataNotAvailable();

    void onListsUpdated();
}
