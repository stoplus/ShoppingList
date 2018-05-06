package com.example.den.shoppinglist.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.den.shoppinglist.AppDatabase;
import com.example.den.shoppinglist.interfaces.ListProductDao;
import com.example.den.shoppinglist.interfaces.ListsDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    public ListProductDao provideListProductDao(AppDatabase appDatabase){
        return appDatabase.listProductDao();
    }
}
