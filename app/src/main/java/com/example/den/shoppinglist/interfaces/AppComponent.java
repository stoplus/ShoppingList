package com.example.den.shoppinglist.interfaces;

import com.example.den.shoppinglist.RequestsLists;
import com.example.den.shoppinglist.module.RequestsListsModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RequestsListsModule.class})
public interface AppComponent {
    RequestsLists getRequestsLists();
}
