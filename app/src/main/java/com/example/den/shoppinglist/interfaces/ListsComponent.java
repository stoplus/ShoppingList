package com.example.den.shoppinglist.interfaces;

import com.example.den.shoppinglist.RequestsLists;
import com.example.den.shoppinglist.module.ListsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ListsModule.class})
public interface ListsComponent {
    void inject(RequestsLists RequestsLists);
}
