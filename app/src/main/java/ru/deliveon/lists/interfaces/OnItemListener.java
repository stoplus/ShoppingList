package ru.deliveon.lists.interfaces;

import android.view.View;

import java.util.List;

import ru.deliveon.lists.database.entity.Product;

public interface OnItemListener {
    void onItemClick(int position, View v, List<Product> list);
    void onItemLongClick(int position, View v, List<Product> list);
    void onRemoveItem(int position, View v);
    void onRemoveItemPurchase(int position, View v);
    void onCheckSortNum(List<Product> list);
}
