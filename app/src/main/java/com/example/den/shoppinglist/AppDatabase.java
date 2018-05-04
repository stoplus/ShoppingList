package com.example.den.shoppinglist;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import com.example.den.shoppinglist.entity.ListProduct;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

@Database(entities = {Lists.class, ListProduct.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ListsDao listsDao();
    public abstract ListProductDao listProductDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
