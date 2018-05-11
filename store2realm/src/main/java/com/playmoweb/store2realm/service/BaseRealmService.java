package com.playmoweb.store2realm.service;

import com.playmoweb.store2realm.dao.RealmDao;
import com.playmoweb.store2realm.model.HasId;
import com.playmoweb.store2store.store.StoreService;

import io.realm.RealmObject;

/**
 * This class try to facilitate usage of Realm with any other async storage system.
 * For now this abstract class implements the most basics CRUD operations only.
 *
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    31/01/2017
 */
public class BaseRealmService<T extends RealmObject & HasId> extends StoreService<T> {
    /**
     * Public constructor
     *
     * @param clazz
     */
    public BaseRealmService(Class<T> clazz) {
        super(clazz, new RealmDao<>(clazz));
    }

    public void setIdKey(String name){
        ((RealmDao) getDao()).setIdKey(name);
    }
}
