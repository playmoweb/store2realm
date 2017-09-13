package com.playmoweb.store2realm.example.injection.component;

import android.app.Application;
import android.content.Context;

import com.playmoweb.store2realm.example.ExampleApplication;
import com.playmoweb.store2realm.example.data.services.PostService;
import com.playmoweb.store2realm.example.injection.context.ApplicationContext;
import com.playmoweb.store2realm.example.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * ApplicationComponent
 *
 * @author Playmoweb
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(ExampleApplication webCampDayApplication);

    @ApplicationContext
    Context context();

    Application application();

    // services
    PostService postService();
}
