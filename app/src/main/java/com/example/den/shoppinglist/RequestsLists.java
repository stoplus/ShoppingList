package com.example.den.shoppinglist;

import com.example.den.shoppinglist.entity.ListProduct;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackListProduct;
import com.example.den.shoppinglist.interfaces.DatabaseCallbackLists;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

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

    public void getLists(final DatabaseCallbackLists databaseCallbackLists) {
        listsDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Lists>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<Lists> lists) throws Exception {
                databaseCallbackLists.onListsLoaded(lists);
            }
        });
    }

    public void getListProduct(final DatabaseCallbackListProduct databaseCallbackListProduct) {
        listProductDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ListProduct>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<ListProduct> listProduct) throws Exception {
                databaseCallbackListProduct.onListsLoaded(listProduct);
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

    public void addListProduct(final DatabaseCallbackListProduct databaseCallbackListProduct, final ListProduct listProduct) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listProductDao.insert(listProduct);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackListProduct.onListsAdded();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackListProduct.onDataNotAvailable();
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

    public void deleteListProduct(final DatabaseCallbackListProduct databaseCallbackListProduct, final ListProduct listProduct) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listProductDao.delete(listProduct);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackListProduct.onListsDeleted();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackListProduct.onDataNotAvailable();
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

    public void updateListProduct(final DatabaseCallbackListProduct databaseCallbackListProduct, final ListProduct listProduct) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                listProductDao.update(listProduct);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallbackListProduct.onListsUpdated();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallbackListProduct.onDataNotAvailable();
            }
        });
    }
}
