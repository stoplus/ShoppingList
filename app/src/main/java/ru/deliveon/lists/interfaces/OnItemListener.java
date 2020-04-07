package ru.deliveon.lists.interfaces;

import android.view.View;

public interface OnItemListener {
    void onItemClick(int position, View v);
    void onItemLongClick(int position, View v);
    void onRemoveItem(int position, View v);
}
