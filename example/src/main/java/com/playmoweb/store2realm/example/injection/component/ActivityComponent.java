package com.playmoweb.store2realm.example.injection.component;

import com.playmoweb.store2realm.example.injection.context.PerActivity;
import com.playmoweb.store2realm.example.injection.module.ActivityModule;
import com.playmoweb.store2realm.example.ui.MainActivity;

import dagger.Component;

/**
 * ActivityComponent
 * This component inject dependencies to all Activities across the application
 *
 * @author Playmoweb
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);
}