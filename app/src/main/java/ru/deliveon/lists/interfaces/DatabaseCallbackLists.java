package ru.deliveon.lists.interfaces;

import java.util.List;

import ru.deliveon.lists.entity.Lists;
import ru.deliveon.lists.entity.ProductForList;

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
