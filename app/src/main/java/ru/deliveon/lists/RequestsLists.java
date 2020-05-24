package ru.deliveon.lists;

import android.util.Log;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.deliveon.lists.di.App;
import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.database.entity.ProductForList;
import ru.deliveon.lists.interfaces.DatabaseCallbackLists;
import ru.deliveon.lists.interfaces.DatabaseCallbackProduct;
import ru.deliveon.lists.database.dao.ListsDao;
import ru.deliveon.lists.database.dao.ProductDao;
import ru.deliveon.lists.database.dao.ProductForListDao;

public class RequestsLists {
    @Inject
    ListsDao listsDao;
    @Inject
    ProductDao productDao;
    @Inject
    ProductForListDao productForListDao;
    public Disposable dispos;
    public Disposable disposable;
    public Disposable dispListId;
    public Disposable dispSameId;
    public Disposable dispSameIdList;
    public Disposable dispInOtherLists;
    private boolean flag = false;

    public void getLists(final DatabaseCallbackLists databaseCallbackLists) {
        Log.d("RequestsListsClass", "getLists ");
        dispos = listsDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lists -> {
                    Log.d("RequestsListsClass", "getLists1");
                    databaseCallbackLists.onListsLoaded(lists);
                });
    }

    public void addLists(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(() -> listsDao.insert(lists))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
//                        getLists(databaseCallbackLists);
                        Log.d("MainActivityclass", "onListsAdded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MainActivityclass", "onError");
                    }
                });
    }

    public void deleteList(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(() -> listsDao.delete(lists))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackLists.onListDeleted(lists.id);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MainActivityclass", "onError");
                    }
                });
    }

    public void updateLists(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(() -> listsDao.update(lists))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.d("MainActivityclass", "updateLists");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("MainActivityclass", "onError");
            }
        });
    }

    public void updateList(final DatabaseCallbackLists databaseCallbackLists, final List<Lists> lists) {
        Completable.fromAction(() -> listsDao.updateList(lists))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("MainActivityclass", "updateList");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MainActivityclass", "onError");
                    }
                });
    }

    //===============================================================================================================
    public void getAllForList(final DatabaseCallbackProduct databaseCallbackProduct, int id) {
        //on my phone there was a fall due to cleaning resources
        if (productDao == null) {
            flag = true;
            RequestsLists requestsLists = App.app().getComponent().getRequestsLists();
            App.app().getListComponent().inject(requestsLists);
        }
        disposable = productDao.getAllFromList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productList -> {
                    if (flag) {
                        Products products = new Products();
                        products.onRestart();
                        flag = false;
                    }
                    databaseCallbackProduct.onListProductsLoaded(productList);
                });
    }

    public void getLastProductId(final DatabaseCallbackLists databaseCallbackLists,
                                 final DatabaseCallbackProduct databaseCallbackProduct, int idList) {
        dispListId = productDao.getLastProductId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(databaseCallbackProduct::onLastProduct);
                .subscribe(lastProductId -> {
                    Log.d("Productsclass", "onLastProduct");
                    dispListId.dispose();
                    // add to the table "goods in the list"
                    ProductForList productForList = new ProductForList(idList, lastProductId);
                    addProductForList(databaseCallbackLists, databaseCallbackProduct, productForList);
                });
    }

    // added the product to the product table
    public void addProduct(final DatabaseCallbackLists databaseCallbackLists,
                           final DatabaseCallbackProduct databaseCallbackProduct,
                           final Product product,
                           int idList) {
        Completable.fromAction(() -> productDao.insertProduct(product))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("RequestsListsClass", "addListProduct1");
                        // get the last added product
                        getLastProductId(databaseCallbackLists, databaseCallbackProduct, idList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Productsclass", "onDataNotAvailable");
                    }
                });
    }

    public void deleteProduct(final DatabaseCallbackProduct databaseCallbackProduct, final Product product, int idList) {
        Completable.fromAction(() -> productDao.delete(product))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Productsclass", "onProductDeleted");
                        getAllForList(databaseCallbackProduct, idList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Productsclass", "onDataNotAvailable");
                    }
                });
    }

    public void deleteProductList(final DatabaseCallbackLists databaseCallbackLists, final List<Integer> list) {
        Completable.fromAction(() -> productDao.deleteByIdList(list))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("MainActivityclass", "onDeletedProductList");
                        databaseCallbackLists.productRemoved();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MainActivityclass", "onError");
                    }
                });
    }

    public void updateProduct(final DatabaseCallbackProduct databaseCallbackProduct, final Product product, int id) {
        Completable.fromAction(() -> productDao.update(product))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("RequestsListsClass", "onSubscribe");
                    }

                    @Override
                    public void onComplete() {
//                        databaseCallbackProduct.onProductUpdated();
                        getAllForList(databaseCallbackProduct, id);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Productsclass", "onDataNotAvailable");
                    }
                });
    }

    public void updateListProduct(final DatabaseCallbackProduct databaseCallbackProduct,
                                  final List<Product> productList, int id) {
        Completable.fromAction(() -> productDao.updateList(productList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (id != 0){
                            getAllForList(databaseCallbackProduct, id);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Productsclass", "onDataNotAvailable");
                    }
                });
    }

    //===============================================================================================================
    public void addProductForList(final DatabaseCallbackLists databaseCallbackLists,
                                  final DatabaseCallbackProduct databaseCallbackProduct, final ProductForList productForList) {
        Completable.fromAction(() -> productForListDao.insert(productForList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (databaseCallbackProduct != null){
                            databaseCallbackProduct.onProductForListAdded();
                            Log.d("Productsclass", "onProductForListAdded");
                        }else {
                            databaseCallbackLists.onProductImported();
                            Log.d("Productsclass", "onProductForListAdded - import");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Productsclass", "onDataNotAvailable");
                    }
                });
    }

    public void deleteProductForList(final DatabaseCallbackProduct databaseCallbackProduct, final int idProduct, final int idList) {
        Completable.fromAction(() -> productForListDao.deleteByIdListAndIdProduct(idProduct, idList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackProduct.onProductForListDeleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Productsclass", "onDataNotAvailable");
                    }
                });
    }

    //remove the list of records from the productForList table
    public void deleteListProductForList(final DatabaseCallbackLists databaseCallbackLists, final List<ProductForList> list) {
        Completable.fromAction(() -> productForListDao.deleteList(list))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackLists.onDeletedListProductForList();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MainActivityclass", "onError");
                    }
                });
    }

    public void getSameIdProductForList(final DatabaseCallbackProduct databaseCallbackProduct, int idProduct) {
        Log.d("RequestsListsClass", "getSameIdProductForList ");
        dispSameId = productForListDao.getSameIdProductForList(idProduct)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    Log.d("RequestsListsClass", "getSameIdProductForList1 ");
                    databaseCallbackProduct.onSameIdProductForList(list);
                });
    }

    public void getSameIdListForList(final DatabaseCallbackLists databaseCallbackLists, int idList) {
        Log.d("RequestsListsClass", "getSameIdListForList ");
        dispSameIdList = productForListDao.getSameIdListForList(idList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    Log.d("RequestsListsClass", "getSameIdListForList ");
                    databaseCallbackLists.onSameIdList(list);
                });
    }

    public void getInOtherLists(final DatabaseCallbackLists databaseCallbackLists, List<Integer> list) {
        Log.d("RequestsListsClass", "getInOtherLists ");
        dispInOtherLists = productForListDao.getInOtherLists(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list1 -> {
                    Log.d("RequestsListsClass", "getInOtherLists ");
                    databaseCallbackLists.onInOtherLists(list1);
                });
    }
}
