package com.example.den.shoppinglist;

import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.DatabaseCallback;
import com.example.den.shoppinglist.interfaces.ListsDao;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RequestsLists {

    public void getUsers(final DatabaseCallback databaseCallback, final ListsDao listsDao) {
        listsDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Lists>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<Lists> lists) throws Exception {
                databaseCallback.onListsLoaded(lists);
            }
        });
    }

    public void addUser(final DatabaseCallback databaseCallback, final Lists lists, final ListsDao listsDao) {
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
                databaseCallback.onListsAdded();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }

    public void deleteLists(final DatabaseCallback databaseCallback, final Lists lists, final ListsDao listsDao) {
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
                databaseCallback.onListsDeleted();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }

    public void updateLists(final DatabaseCallback databaseCallback, final Lists lists, final ListsDao listsDao) {
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
                databaseCallback.onListsUpdated();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }
}
