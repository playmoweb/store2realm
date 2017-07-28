package com.playmoweb.store2realm.service;

import com.playmoweb.store2store.dao.IStoreDaoRx2;
import com.playmoweb.store2store.service.AbstractServiceRx2;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import io.reactivex.Observable;
import io.realm.RealmObject;

/**
 * Created by hoanghiep on 7/28/17.
 */

public abstract class BaseRealmServiceRx2<T extends RealmObject> extends AbstractServiceRx2<T> {
    /**
     * Public constructor
     *
     * @param clazz
     * @param storage
     */
    public BaseRealmServiceRx2(Class<T> clazz, IStoreDaoRx2<T> storage) {
        super(clazz, storage);
    }

    @Override
    protected Observable<List<T>> getAll(Filter filter, SortingMode sortingMode) {
        return null;
    }

    @Override
    protected Observable<T> getOne(Filter filter, SortingMode sortingMode) {
        return null;
    }

    @Override
    protected Observable<T> getById(int id) {
        return null;
    }

    @Override
    protected Observable<T> insert(T object) {
        return null;
    }

    @Override
    protected Observable<List<T>> insert(List<T> items) {
        return null;
    }

    @Override
    protected Observable<T> update(T object) {
        return null;
    }

    @Override
    protected Observable<List<T>> update(List<T> items) {
        return null;
    }

    @Override
    protected Observable<Void> delete(List<T> items) {
        return null;
    }

    @Override
    protected Observable<Void> delete(T object) {
        return null;
    }

    @Override
    protected Observable<Void> deleteAll() {
        return null;
    }
}
