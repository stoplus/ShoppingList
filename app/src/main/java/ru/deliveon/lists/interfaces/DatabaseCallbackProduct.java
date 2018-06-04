package ru.deliveon.lists.interfaces;

import java.util.List;

import ru.deliveon.lists.entity.Product;
import ru.deliveon.lists.entity.ProductForList;

public interface DatabaseCallbackProduct {

    void onListProductsLoaded(List<Product> list);

    void onProductDeleted();

    void onProductForListDeleted();

    void onProductAdded();

    void onProductForListAdded();

    void onDataNotAvailable();

    void onProductUpdated();

    void onLastProduct(int id);

    void onSameIdProductForList(List<ProductForList> list);

    void onUpdateList();

}
