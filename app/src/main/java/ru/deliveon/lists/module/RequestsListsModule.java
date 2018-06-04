package ru.deliveon.lists.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.deliveon.lists.RequestsLists;

@Module
public class RequestsListsModule {

    @Singleton
    @Provides
    public RequestsLists provideRequestsLists(){
        return new RequestsLists();
    }
}
