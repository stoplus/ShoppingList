package com.example.den.shoppinglist.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private Context context;

    public DatabaseModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideContext(){
        return context;
    }

//    @Singleton @Provides
//    public AppDatabase provideAppDatabase(Context context){
//        return Room.databaseBuilder(context, AppDatabase.class, "database").build();
//    }
//
//    @Singleton @Provides
//    public ListsDao provideListsDao(AppDatabase appDatabase){
//        return appDatabase.listsDao();
//    }
//
//    @Singleton @Provides
//    public ProductDao provideListProductDao(AppDatabase appDatabase){
//        return appDatabase.listProductDao();
//    }
}
