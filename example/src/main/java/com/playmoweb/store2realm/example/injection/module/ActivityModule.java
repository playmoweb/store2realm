package com.playmoweb.store2realm.example.injection.module;

import android.app.Activity;
import android.content.Context;

import com.playmoweb.store2realm.example.injection.context.ActivityContext;

import dagger.Module;
import dagger.Provides;

/**
 * ActivityModule
 *
 * @author Playmoweb
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    Context providesContext() {
        return mActivity;
    }
}