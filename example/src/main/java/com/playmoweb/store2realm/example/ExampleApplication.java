package com.playmoweb.store2realm.example;

import android.app.Application;
import android.content.Context;

import com.playmoweb.store2realm.example.injection.component.ApplicationComponent;
import com.playmoweb.store2realm.example.injection.component.DaggerApplicationComponent;
import com.playmoweb.store2realm.example.injection.module.ApplicationModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by thibaud on 28/07/2017.
 */
public class ExampleApplication extends Application {
    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static ExampleApplication get(Context context) {
        return (ExampleApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }
}
