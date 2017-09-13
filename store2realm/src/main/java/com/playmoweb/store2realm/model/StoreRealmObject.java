package com.playmoweb.store2realm.model;

import io.realm.RealmObject;

/**
 * This object is needed to allow operation on unique id in realm.
 * @note    If you don't have specific id (not recommended), you can return null/0 values but
 *          you will be limited in Store methods.
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    31/01/2017
 */
public abstract class StoreRealmObject extends RealmObject {
    public abstract int getUniqueIdentifier();
    public abstract String getUniqueIdentifierName();
}
