package com.playmoweb.store2realm.example.injection.module;

import android.app.Application;
import android.content.Context;

import com.playmoweb.store2realm.example.data.ApiService;
import com.playmoweb.store2realm.example.injection.context.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies. Mainly singleton object that can be injected from
 * anywhere in the app.
 *
 * @author Playmoweb
 */
@Module
public class ApplicationModule {

    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    ApiService provideApiService() {
        return ApiService.Creator.buildApiService();
    }

}
