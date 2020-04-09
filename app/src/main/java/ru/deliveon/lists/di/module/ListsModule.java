package ru.deliveon.lists.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.deliveon.lists.database.AppDatabase;
import ru.deliveon.lists.database.dao.ListsDao;
import ru.deliveon.lists.database.dao.ProductDao;
import ru.deliveon.lists.database.dao.ProductForListDao;

import static ru.deliveon.lists.database.AppDatabase.MIGRATION_1_2;

@Module
public class ListsModule {
    private Context context;

    public ListsModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideContext(){
        return context;
    }

    @Singleton @Provides
    public AppDatabase provideAppDatabase(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "database")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    @Singleton @Provides
    public ListsDao provideListsDao(AppDatabase appDatabase){
        return appDatabase.listsDao();
    }

    @Singleton @Provides
    public ProductDao provideListProductDao(AppDatabase appDatabase){
        return appDatabase.listProductDao();
    }

    @Singleton @Provides
    public ProductForListDao provideProductForListDao(AppDatabase appDatabase){
        return appDatabase.productForListDao();
    }
}
