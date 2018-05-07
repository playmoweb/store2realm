package com.playmoweb.store2realm.model;

/**
 * Created by thibaud on 13/09/2017.
 */
public interface HasId<T> {
    T getUniqueIdentifier();
    String getUniqueIdentifierName();
}
