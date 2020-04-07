package ru.deliveon.lists.interfaces;

import ru.deliveon.lists.database.entity.Lists;

public interface AddEditListInterface {
    void addList(Lists lists);
    void update(Lists lists);
}
