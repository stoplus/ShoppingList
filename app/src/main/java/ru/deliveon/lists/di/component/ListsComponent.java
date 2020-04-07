package ru.deliveon.lists.di.component;

import javax.inject.Singleton;

import dagger.Component;
import ru.deliveon.lists.RequestsLists;
import ru.deliveon.lists.di.module.ListsModule;

@Singleton
@Component(modules = {ListsModule.class})
public interface ListsComponent {
    void inject(RequestsLists RequestsLists);
}
