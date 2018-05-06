package com.example.den.shoppinglist;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import com.example.den.shoppinglist.entity.Product;
import com.example.den.shoppinglist.entity.Lists;
import com.example.den.shoppinglist.entity.ProductForList;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;
import com.example.den.shoppinglist.interfaces.ProductForListDao;

@Database(entities = {Lists.class, Product.class, ProductForList.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ListsDao listsDao();
    public abstract ListProductDao listProductDao();
    public abstract ProductForListDao productForListDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
