package ru.deliveon.lists.interfaces;

import javax.inject.Singleton;

import dagger.Component;
import ru.deliveon.lists.RequestsLists;
import ru.deliveon.lists.module.RequestsListsModule;

@Singleton
@Component(modules = {RequestsListsModule.class})
public interface AppComponent {
    RequestsLists getRequestsLists();
}
