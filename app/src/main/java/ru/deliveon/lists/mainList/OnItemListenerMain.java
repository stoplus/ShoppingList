package ru.deliveon.lists.mainList;

import android.view.View;

import java.util.List;

import ru.deliveon.lists.database.entity.Lists;

public interface OnItemListenerMain {
    void onItemClick(int position, View v, List<Lists> list);
    void onItemLongClick(int position, View v, List<Lists> list);
    void onRemoveItem(int position, View v, List<Lists> list);
    void onCheckSortNum(List<Lists> list);
}
