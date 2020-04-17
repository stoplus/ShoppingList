package ru.deliveon.lists.interfaces;

import java.util.List;

import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.database.entity.ProductForList;

public interface DatabaseCallbackProduct {

    void onListProductsLoaded(List<Product> list);

    void onProductForListDeleted();

    void onProductForListAdded();

    void onSameIdProductForList(List<ProductForList> list);
}
