package ru.deliveon.lists.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import ru.deliveon.lists.database.entity.Lists;
import ru.deliveon.lists.database.entity.Product;
import ru.deliveon.lists.database.entity.ProductForList;
import ru.deliveon.lists.database.dao.ListsDao;
import ru.deliveon.lists.database.dao.ProductDao;
import ru.deliveon.lists.database.dao.ProductForListDao;

@Database(entities = {Lists.class, Product.class, ProductForList.class}, version = 2)
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

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Product ADD COLUMN sort_num INTEGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE Lists ADD COLUMN sort_num INTEGER DEFAULT 0 NOT NULL");
            database.execSQL("ALTER TABLE Lists ADD COLUMN color INTEGER DEFAULT 0 NOT NULL");
        }
    };
}
