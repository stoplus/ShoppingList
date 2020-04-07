package ru.deliveon.lists.di;

import android.app.Application;

import ru.deliveon.lists.di.component.AppComponent;

import ru.deliveon.lists.di.component.DaggerAppComponent;
import ru.deliveon.lists.di.component.DaggerListsComponent;
import ru.deliveon.lists.di.component.ListsComponent;
import ru.deliveon.lists.di.module.ListsModule;
import ru.deliveon.lists.di.module.RequestsListsModule;

public class App extends Application {
    private static App app;
    private AppComponent appComponent;
    private ListsComponent listsComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appComponent = DaggerAppComponent.builder()
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