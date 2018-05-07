package com.example.den.shoppinglist;

import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.entity.ProductForList;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackListProduct;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackLists;
import com.example.den.shoppinglist.interfaces.ListProductDao;
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

public class RequestsLists  {
    @Inject
    ListsDao listsDao;
    @Inject
    ListProductDao listProductDao;
    @Inject
    ProductForListDao productForListDao;
    public Disposable dispos; //переменная для отписывания наблюдателя
    public Disposable disposable; //переменная для отписывания наблюдателя
    public Disposable dispListId;
    public Disposable dispSameId;

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

    public void deleteLists(final DatabaseCallbackLists databaseCallbackLists, final Lists lists) {
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
                        databaseCallbackLists.onListsDeleted();
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

    public void getAllForList(final DatabaseCallbackListProduct databaseCallbackListProduct, int id){
        disposable = listProductDao.getAllFromList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Product>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<Product> product) throws Exception {
                        databaseCallbackListProduct.onListProductsLoaded(product);
                    }
                });
    }

    public void getlastProduct(final DatabaseCallbackListProduct databaseCallbackListProduct) {
        dispListId = listProductDao.getlastProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Integer id) throws Exception {
                        databaseCallbackListProduct.onLastProduct(id);
                    }
                });
    }

    public void addListProduct(final DatabaseCallbackListProduct databaseCallbackListProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listProductDao.insert(product);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackListProduct.onProductAdded();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackListProduct.onDataNotAvailable();
            }
        });
    }

    public void deleteProduct(final DatabaseCallbackListProduct databaseCallbackListProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listProductDao.delete(product);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackListProduct.onProductDeleted();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackListProduct.onDataNotAvailable();
            }
        });
    }

    public void updateListProduct(final DatabaseCallbackListProduct databaseCallbackListProduct, final Product product) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listProductDao.update(product);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackListProduct.onProductUpdated();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackListProduct.onDataNotAvailable();
            }
        });
    }

    //===============================================================================================================
    public void addProductForList(final DatabaseCallbackListProduct databaseCallbackListProduct, final ProductForList productForList) {
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
                        databaseCallbackListProduct.onProductForListAdded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackListProduct.onDataNotAvailable();
                    }
                });
    }

    public void deleteProductForList(final DatabaseCallbackListProduct databaseCallbackListProduct, final int idProduct, final int idList) {
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
                        databaseCallbackListProduct.onProductForListDeleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        databaseCallbackListProduct.onDataNotAvailable();
                    }
                });
    }

    public void getSameIdProductForList(final DatabaseCallbackListProduct databaseCallbackListProduct, int id) {
        dispSameId = productForListDao.getSameIdProductForList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductForList>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull List<ProductForList> list) throws Exception {
                        databaseCallbackListProduct.onSameIdProductForList(list);
                    }
                });
    }
}
