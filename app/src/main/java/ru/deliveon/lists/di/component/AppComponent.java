package ru.deliveon.lists.di.component;

import javax.inject.Singleton;

import dagger.Component;
import ru.deliveon.lists.RequestsLists;
import ru.deliveon.lists.di.module.RequestsListsModule;

@Singleton
@Component(modules = {RequestsListsModule.class})
public interface AppComponent {
    RequestsLists getRequestsLists();
}
