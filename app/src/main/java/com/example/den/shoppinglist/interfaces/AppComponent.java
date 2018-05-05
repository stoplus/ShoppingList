package com.example.den.shoppinglist.interfaces;

import com.example.den.shoppinglist.MainActivity;
import com.example.den.shoppinglist.module.DatabaseModule;
import com.example.den.shoppinglist.module.RequestsListsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RequestsListsModule.class})
//@Component(modules = {DatabaseModule.class, RequestsListsModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
