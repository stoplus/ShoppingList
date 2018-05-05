package com.example.den.shoppinglist.module;

import com.example.den.shoppinglist.RequestsLists;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RequestsListsModule {

    @Singleton
    @Provides
    public RequestsLists provideRequestsLists(){
        return new RequestsLists();
    }
}
