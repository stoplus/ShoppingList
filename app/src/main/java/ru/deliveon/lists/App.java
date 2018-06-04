package ru.deliveon.lists;

import android.app.Application;

import ru.deliveon.lists.interfaces.AppComponent;
import ru.deliveon.lists.interfaces.DaggerAppComponent;
import ru.deliveon.lists.interfaces.DaggerListsComponent;
import ru.deliveon.lists.interfaces.ListsComponent;
import ru.deliveon.lists.module.ListsModule;
import ru.deliveon.lists.module.RequestsListsModule;

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