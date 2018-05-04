package com.example.den.shoppinglist.interfaces;

import com.example.den.shoppinglist.MainActivity;
import com.example.den.shoppinglist.module.DatabaseModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {DatabaseModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
