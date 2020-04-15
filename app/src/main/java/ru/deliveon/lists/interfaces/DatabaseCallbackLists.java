package ru.deliveon.lists.interfaces;

import java.util.List;

import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.database.entity.ProductForList;

public interface DatabaseCallbackLists {

    void onListsLoaded(List<Lists> lists);

    void onListDeleted();

    void onSameIdList(List<ProductForList> list);

    void onDeletedListProductForList();

    void onInOtherLists(List<ProductForList> list);

    void productRemoved();
}
