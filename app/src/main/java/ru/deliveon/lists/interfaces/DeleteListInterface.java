package ru.deliveon.lists.interfaces;

import ru.deliveon.lists.database.entity.Lists;

public interface DeleteListInterface {
    void deleteList(Lists lists);
    void cancelDeleteList();
}
