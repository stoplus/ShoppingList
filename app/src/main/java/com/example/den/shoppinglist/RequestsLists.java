package com.example.den.shoppinglist;

import android.util.Log;

import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.entity.ProductForList;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackProduct;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackLists;
import com.example.den.shoppinglist.interfaces.ProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;
import com.example.den.shoppinglist.interfaces.ProductForListDao;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RequestsLists {
    @Inject
    ListsDao listsDao;
    @Inject
    ProductDao productDao;
    @Inject
    ProductForListDao productForListDao;
    public Disposable dispos; //переменная для отписывания наблюдателя
    public Disposable disposable; //переменная для отписывания наблюдателя
    public Disposable dispListId;
    public Disposable dispSameId;
    public Disposable dispInOtherLists;
    private  boolean flag = false;

    public void getLists(final DatabaseCallbackLists databaseCallbackLists) {
        dispos = listsDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Lists>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<Lists> lists) throws Exception {
                        databaseCallbackLists.onListsLoaded(lists);
                    }
                });
    }

    public void addLists(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
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
            public void run() throws Exception {
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
            public void run() throws Exception {
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
        //на моем телефоне здесь было падение из-за очистки ресурсов
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
                    public void accept(@io.reactivex.annotations.NonNull List<Product> product) throws Exception {
                        if (flag){
                            Products products = new Products();
                            products.updateTwoLists(product);
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
                    public void accept(@io.reactivex.annotations.NonNull Integer id) throws Exception {
                        databaseCallbackProduct.onLastProduct(id);
                    }
                });
    }

    public void addListProduct(final DatabaseCallbackProduct databaseCallbackProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
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
            public void run() throws Exception {
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
            public void run() throws Exception {
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
            public void run() throws Exception {
                productDao.update(product);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
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
            public void run() throws Exception {
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
            public void run() throws Exception {
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
            public void run() throws Exception {
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

    //удаляем список записей из таблици productForList
    public void deleteListProductForList(final DatabaseCallbackLists databaseCallbackLists, final List<ProductForList> list) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
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
        dispSameId = productForListDao.getSameIdProductForList(idProduct)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) throws Exception {
                        databaseCallbackProduct.onSameIdProductForList(list);
                    }
                });
    }

    public void getSameIdListForList(final DatabaseCallbackLists databaseCallbackLists, int idList) {
        dispSameId = productForListDao.getSameIdListForList(idList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) throws Exception {
                        databaseCallbackLists.onSameIdList(list);
                    }
                });
    }

    public void getInOtherLists(final DatabaseCallbackLists databaseCallbackLists, List<Integer> list) {
        dispInOtherLists = productForListDao.getInOtherLists(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) throws Exception {
                        databaseCallbackLists.onInOtherLists(list);
                    }
                });
    }
}
