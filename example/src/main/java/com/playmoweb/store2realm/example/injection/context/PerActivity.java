package com.playmoweb.store2realm.example.injection.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the DataManager to be memorised in the
 * correct component.
 *
 * @author Playmoweb
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}