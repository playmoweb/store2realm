package com.playmoweb.store2realm.dao;

import com.playmoweb.store2store.dao.IStoreDao;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortType;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.Date;
import java.util.List;
import java.util.UnknownFormatFlagsException;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

/**
 * Realm store implementation
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017.
 */
public class RealmDao<T extends RealmObject> implements IStoreDao<T> {
    protected Class<T> clazz;

    public RealmDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get one with a specific filter object
     * @param filter
     * @return
     */
    @Override
    public final Observable<T> getOne(Filter filter, SortingMode sortingMode) {
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<T> query = realm.where(clazz);
        query = filterToQuery(filter, query);

        T item;

        if(sortingMode != null) {
            RealmResults<T> items = query.findAllSorted(sortingMode.key, convertToSort(sortingMode.sort));
            item = items.first();
        } else {
            item = query.findFirst();
        }

        T copy = null;
        if(item != null) {
            copy = realm.copyFromRealm(item);
        }
        realm.close();

        return Observable.just(copy);
    }

    /**
     * Get one by id
     * @param id
     * @return
     */
    @Override
    public Observable<T> getById(String id) {
        return getOne(new Filter("id", id), null);
    }

    /**
     * Get all for a specific filters
     * @return
     */
    @Override
    public final Observable<List<T>> getAll(Filter filter, SortingMode sortingMode) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> query = realm.where(clazz);
        query = filterToQuery(filter, query);

        RealmResults<T> items;
        if(sortingMode != null) {
            items = query.findAllSorted(sortingMode.key, convertToSort(sortingMode.sort));
        } else {
            items = query.findAll();
        }
        List<T> copies = realm.copyFromRealm(items);
        realm.close();

        return Observable.just(copies);
    }

    /**
     * Insert one object
     * @return object inserted
     */
    @Override
    public final Observable<T> insertOrUpdate(T object) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        T inserted = realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        T copy = realm.copyFromRealm(inserted);
        realm.close();

        return Observable.just(copy);
    }

    /**
     * Insert or update all
     * @param items
     * @return List of item copied from realm
     */
    @Override
    public final Observable<List<T>> insertOrUpdate(List<T> items) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        items = realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
        List<T> copies = realm.copyFromRealm(items);
        realm.close();

        return Observable.just(copies);
    }

    /**
     * Remove only these items
     * @param items
     * @return List of item copied from realm
     */
    @Override
    public final Observable<Void> delete(List<T> items) {
        for(T elem : items) {
            if(elem.isManaged()) {
                elem.deleteFromRealm(); // potentially slow
            }
        }
        return Observable.just(null);
    }

    @Override
    public final Observable<Void> delete(T object) {
        if(object.isManaged()) {
            object.deleteFromRealm();
        }
        return Observable.just(null);
    }

    @Override
    public Observable<Void> deleteAll() {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(clazz);
        realm.commitTransaction();
        realm.close();

        return Observable.just(null);
    }

    /**************************************************************************
     *   Utils
     *************************************************************************/

    /**
     * For all keys in filter add them to query
     * @param filter
     * @param query
     * @return
     */
    public RealmQuery<T> filterToQuery(Filter filter, RealmQuery<T> query) {
        if(filter != null) {
            for (String key : filter.keySet()) {
                if (filter.containsKey(key)) {
                    query = addRealmFilter(query, key, filter.get(key));
                }
            }
        }
        return query;
    }

    /**
     * Pick the right realm method depending on filter
     * @note    Repeating code is volonteer for performances (direct method call is faster than invoke())
     * @param query
     * @param key
     * @param kvp
     * @return
     */
    private RealmQuery<T> addRealmFilter(RealmQuery<T> query, String key, Filter.KeyValuePair kvp) {
        switch (kvp.filterType) {
            case EQUAL:
                if(kvp.value instanceof String)
                    return query.equalTo(key, (String) kvp.value);
                if(kvp.value instanceof Integer)
                    return query.equalTo(key, (Integer) kvp.value);
                if(kvp.value instanceof Boolean)
                    return query.equalTo(key, (Boolean) kvp.value);
                if(kvp.value instanceof Double)
                    return query.equalTo(key, (Double) kvp.value);
                if(kvp.value instanceof Date)
                    return query.equalTo(key, (Date) kvp.value);
                if(kvp.value instanceof Float)
                    return query.equalTo(key, (Float) kvp.value);
                if(kvp.value instanceof Long)
                    return query.equalTo(key, (Long) kvp.value);
                if(kvp.value instanceof Byte)
                    return query.equalTo(key, (Byte) kvp.value);
            case NOT_EQUAL:
                if(kvp.value instanceof String)
                    return query.notEqualTo(key, (String) kvp.value);
                if(kvp.value instanceof Integer)
                    return query.notEqualTo(key, (Integer) kvp.value);
                if(kvp.value instanceof Boolean)
                    return query.notEqualTo(key, (Boolean) kvp.value);
                if(kvp.value instanceof Double)
                    return query.notEqualTo(key, (Double) kvp.value);
                if(kvp.value instanceof Date)
                    return query.notEqualTo(key, (Date) kvp.value);
                if(kvp.value instanceof Float)
                    return query.notEqualTo(key, (Float) kvp.value);
                if(kvp.value instanceof Long)
                    return query.notEqualTo(key, (Long) kvp.value);
                if(kvp.value instanceof Byte)
                    return query.notEqualTo(key, (Byte) kvp.value);
            case GREATER_THAN:
                if(kvp.value instanceof Integer)
                    return query.greaterThan(key, (Integer) kvp.value);
                if(kvp.value instanceof Double)
                    return query.greaterThan(key, (Double) kvp.value);
                if(kvp.value instanceof Date)
                    return query.greaterThan(key, (Date) kvp.value);
                if(kvp.value instanceof Float)
                    return query.greaterThan(key, (Float) kvp.value);
                if(kvp.value instanceof Long)
                    return query.greaterThan(key, (Long) kvp.value);
                if(kvp.value instanceof Byte)
                    return query.greaterThan(key, (Byte) kvp.value);
            case LESS_THAN:
                if(kvp.value instanceof Integer)
                    return query.lessThan(key, (Integer) kvp.value);
                if(kvp.value instanceof Double)
                    return query.lessThan(key, (Double) kvp.value);
                if(kvp.value instanceof Date)
                    return query.lessThan(key, (Date) kvp.value);
                if(kvp.value instanceof Float)
                    return query.lessThan(key, (Float) kvp.value);
                if(kvp.value instanceof Long)
                    return query.lessThan(key, (Long) kvp.value);
                if(kvp.value instanceof Byte)
                    return query.lessThan(key, (Byte) kvp.value);
            case GREATER_THAN_OR_EQUAL:
                if(kvp.value instanceof Integer)
                    return query.greaterThanOrEqualTo(key, (Integer) kvp.value);
                if(kvp.value instanceof Double)
                    return query.greaterThanOrEqualTo(key, (Double) kvp.value);
                if(kvp.value instanceof Date)
                    return query.greaterThanOrEqualTo(key, (Date) kvp.value);
                if(kvp.value instanceof Float)
                    return query.greaterThanOrEqualTo(key, (Float) kvp.value);
                if(kvp.value instanceof Long)
                    return query.greaterThanOrEqualTo(key, (Long) kvp.value);
                if(kvp.value instanceof Byte)
                    return query.greaterThanOrEqualTo(key, (Byte) kvp.value);
            case LESS_THAN_OR_EQUAL:
                if(kvp.value instanceof Integer)
                    return query.greaterThanOrEqualTo(key, (Integer) kvp.value);
                if(kvp.value instanceof Double)
                    return query.greaterThanOrEqualTo(key, (Double) kvp.value);
                if(kvp.value instanceof Date)
                    return query.greaterThanOrEqualTo(key, (Date) kvp.value);
                if(kvp.value instanceof Float)
                    return query.greaterThanOrEqualTo(key, (Float) kvp.value);
                if(kvp.value instanceof Long)
                    return query.greaterThanOrEqualTo(key, (Long) kvp.value);
                if(kvp.value instanceof Byte)
                    return query.greaterThanOrEqualTo(key, (Byte) kvp.value);
        }

        throw new UnknownFormatFlagsException("Instance of the value is unknow or this type is unknow : " + kvp.filterType.toString());
    }

    /**
     * Convert SortType to realm Sort
     * @param st
     * @return
     */
    private Sort convertToSort(SortType st) {
        return st == SortType.ASCENDING ? Sort.ASCENDING : Sort.DESCENDING;
    }
}
