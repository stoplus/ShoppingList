package ru.deliveon.lists.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.deliveon.lists.AppDatabase;
import ru.deliveon.lists.interfaces.ListsDao;
import ru.deliveon.lists.interfaces.ProductDao;
import ru.deliveon.lists.interfaces.ProductForListDao;

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
        return Room.databaseBuilder(context, AppDatabase.class, "database").build();
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
