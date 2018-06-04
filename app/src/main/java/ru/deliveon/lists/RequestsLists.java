package ru.deliveon.lists;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.deliveon.lists.entity.Lists;
import ru.deliveon.lists.entity.Product;
import ru.deliveon.lists.entity.ProductForList;
import ru.deliveon.lists.interfaces.DatabaseCallbackLists;
import ru.deliveon.lists.interfaces.DatabaseCallbackProduct;
import ru.deliveon.lists.interfaces.ListsDao;
import ru.deliveon.lists.interfaces.ProductDao;
import ru.deliveon.lists.interfaces.ProductForListDao;

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
                .subscribe(new Consumer<List<Lists>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<Lists> lists) {
                        Log.d("RequestsListsClass", "getLists1");
                        databaseCallbackLists.onListsLoaded(lists);
                    }
                });
    }

    public void addLists(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                listsDao.insert(lists);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackLists.onListsAdded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackLists.onDataNotAvailable();
                    }
                });
    }

    public void deleteList(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                listsDao.delete(lists);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackLists.onListDeleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackLists.onDataNotAvailable();
                    }
                });
    }

    public void updateLists(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                listsDao.update(lists);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackLists.onListsUpdated();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackLists.onDataNotAvailable();
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
                .subscribe(new Consumer<List<Product>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<Product> product) {
                        if (flag) {
                            Products products = new Products();
                            products.onRestart();
                            flag = false;
                        }
                        databaseCallbackProduct.onListProductsLoaded(product);
                    }
                });
    }

    public void getlastProduct(final DatabaseCallbackProduct databaseCallbackProduct) {
        dispListId = productDao.getlastProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Integer id) {
                        databaseCallbackProduct.onLastProduct(id);
                    }
                });
    }

    public void addListProduct(final DatabaseCallbackProduct databaseCallbackProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productDao.insert(product);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("RequestsListsClass", "addListProduct1");
                        databaseCallbackProduct.onProductAdded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackProduct.onDataNotAvailable();
                    }
                });
    }

    public void deleteProduct(final DatabaseCallbackProduct databaseCallbackProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productDao.delete(product);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackProduct.onProductDeleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackProduct.onDataNotAvailable();
                    }
                });
    }

    public void deleteProductList(final DatabaseCallbackLists databaseCallbackLists, final List<Integer> list) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productDao.deleteByIdList(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackLists.onDeletedProductList();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackLists.onDataNotAvailable();
                    }
                });
    }

    public void updateProduct(final DatabaseCallbackProduct databaseCallbackProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productDao.update(product);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("RequestsListsClass", "onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackProduct.onProductUpdated();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackProduct.onDataNotAvailable();
                    }
                });
    }

    public void updateListProduct(final DatabaseCallbackProduct databaseCallbackProduct, final List<Product> productList) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productDao.updateList(productList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackProduct.onUpdateList();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackProduct.onDataNotAvailable();
            }
        });
    }

    //===============================================================================================================
    public void addProductForList(final DatabaseCallbackProduct databaseCallbackProduct, final ProductForList productForList) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productForListDao.insert(productForList);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallbackProduct.onProductForListAdded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackProduct.onDataNotAvailable();
                    }
                });
    }

    public void deleteProductForList(final DatabaseCallbackProduct databaseCallbackProduct, final int idProduct, final int idList) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productForListDao.deleteByIdListAndIdProduct(idProduct, idList);
            }
        }).subscribeOn(Schedulers.io())
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
                        databaseCallbackProduct.onDataNotAvailable();
                    }
                });
    }

    //remove the list of records from the productForList table
    public void deleteListProductForList(final DatabaseCallbackLists databaseCallbackLists, final List<ProductForList> list) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                productForListDao.deleteList(list);
            }
        }).subscribeOn(Schedulers.io())
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
                        databaseCallbackLists.onDataNotAvailable();
                    }
                });
    }

    public void getSameIdProductForList(final DatabaseCallbackProduct databaseCallbackProduct, int idProduct) {
        Log.d("RequestsListsClass", "getSameIdProductForList ");
        dispSameId = productForListDao.getSameIdProductForList(idProduct)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) {
                        Log.d("RequestsListsClass", "getSameIdProductForList1 ");
                        databaseCallbackProduct.onSameIdProductForList(list);
                    }
                });
    }

    public void getSameIdListForList(final DatabaseCallbackLists databaseCallbackLists, int idList) {
        Log.d("RequestsListsClass", "getSameIdListForList ");
        dispSameIdList = productForListDao.getSameIdListForList(idList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) {
                        Log.d("RequestsListsClass", "getSameIdListForList ");
                        databaseCallbackLists.onSameIdList(list);
                    }
                });
    }

    public void getInOtherLists(final DatabaseCallbackLists databaseCallbackLists, List<Integer> list) {
        Log.d("RequestsListsClass", "getInOtherLists ");
        dispInOtherLists = productForListDao.getInOtherLists(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) {
                        Log.d("RequestsListsClass", "getInOtherLists ");
                        databaseCallbackLists.onInOtherLists(list);
                    }
                });
    }
}
