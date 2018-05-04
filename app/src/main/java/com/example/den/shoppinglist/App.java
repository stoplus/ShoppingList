package com.example.den.shoppinglist;

import android.app.Application;

import com.example.den.shoppinglist.interfaces.AppComponent;
import com.example.den.shoppinglist.interfaces.DaggerAppComponent;
import com.example.den.shoppinglist.module.DatabaseModule;

public class App extends Application {
    private static App app;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appComponent = DaggerAppComponent.builder()
                .databaseModule(new DatabaseModule(getApplicationContext())).build();
    }

    public static App app(){
        return app;
    }

    public AppComponent getComponent(){
        return appComponent;
    }
}