package ru.deliveon.lists.interfaces;

import javax.inject.Singleton;

import dagger.Component;
import ru.deliveon.lists.RequestsLists;
import ru.deliveon.lists.module.ListsModule;

@Singleton
@Component(modules = {ListsModule.class})
public interface ListsComponent {
    void inject(RequestsLists RequestsLists);
}
