package com.example.den.shoppinglist;

import android.app.Application;

import com.example.den.shoppinglist.interfaces.AppComponent;
import com.example.den.shoppinglist.interfaces.DaggerAppComponent;
import com.example.den.shoppinglist.interfaces.DaggerListsComponent;
import com.example.den.shoppinglist.interfaces.ListsComponent;
import com.example.den.shoppinglist.module.DatabaseModule;
import com.example.den.shoppinglist.module.ListsModule;
import com.example.den.shoppinglist.module.RequestsListsModule;

public class App extends Application {
    private static App app;
    private AppComponent appComponent;
    private ListsComponent listsComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appComponent = DaggerAppComponent.builder()
//                .databaseModule(new DatabaseModule(getApplicationContext()))
                .requestsListsModule(new RequestsListsModule())
                .build();
        listsComponent = DaggerListsComponent.builder()
                .listsModule(new ListsModule(getApplicationContext()))
                .build();
    }

    public static App app(){
        return app;
    }

    public AppComponent getComponent(){
        return appComponent;
    }
    public ListsComponent getListComponent(){ return listsComponent; }
}