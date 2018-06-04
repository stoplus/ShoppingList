package ru.deliveon.lists;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;

import ru.deliveon.lists.entity.Lists;
import ru.deliveon.lists.entity.Product;
import ru.deliveon.lists.entity.ProductForList;
import ru.deliveon.lists.interfaces.ListsDao;
import ru.deliveon.lists.interfaces.ProductDao;
import ru.deliveon.lists.interfaces.ProductForListDao;

@Database(entities = {Lists.class, Product.class, ProductForList.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ListsDao listsDao();
    public abstract ProductDao listProductDao();
    public abstract ProductForListDao productForListDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
