package com.playmoweb.store2realm.dao;

import com.playmoweb.store2realm.model.HasId;
import com.playmoweb.store2store.store.Optional;
import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.FilterType;
import com.playmoweb.store2store.utils.SortType;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatFlagsException;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Realm store implementation
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    08/02/2017
 */
public class RealmDao<T extends RealmObject & HasId> extends StoreDao<T> {
    protected  Class<T> clazz;

    public RealmDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get all for a specific filters
     * @return
     */
    @Override
    public Flowable<Optional<List<T>>> getAll(Filter filter, SortingMode sortingMode) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> query = realm.where(clazz);
        query = filterToQuery(filter, query);

        RealmResults<T> items;
        if(sortingMode != null && sortingMode.entries.size() > 0) {
            items = applySortingMode(sortingMode, query);
        } else {
            items = query.findAll();
        }
        List<T> copies = realm.copyFromRealm(items);
        realm.close();

        return Flowable.just(Optional.wrap(copies));
    }

    @Override
    public Flowable<Optional<List<T>>> getAll(List<T> items) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> query = realm.where(clazz);

        if(items.size() > 0){
            String paramName = items.get(0).getUniqueIdentifierName();
            Integer[] keys = new Integer[items.size()];
            for(int i = 0; i < items.size(); i++){
                keys[i] = items.get(i).getUniqueIdentifier();
            }
            query.in(paramName, keys);
        }

        List<T> copies = realm.copyFromRealm(query.findAll());
        realm.close();

        return Flowable.just(Optional.wrap(copies));
    }

    /**
     * Get one with a specific filter object
     * @param filter
     * @return
     */
    @Override
    public Flowable<Optional<T>> getOne(Filter filter, SortingMode sortingMode) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> query = realm.where(clazz);
        query = filterToQuery(filter, query);
        T item = null;
        if (sortingMode != null) {
            RealmResults<T> items = applySortingMode(sortingMode, query);
            if (!items.isEmpty()) {
                item = items.first();
            }
        } else {
            item = query.findFirst();
        }
        T copy = null;
        if (item != null) {
            copy = realm.copyFromRealm(item);
        }
        realm.close();
        return Flowable.just(Optional.wrap(copy));
    }

    @Override
    public Flowable<Optional<T>> getOne(T item) {
        return getOne(new Filter(item.getUniqueIdentifierName(), FilterType.EQUAL), null);
    }

    /**
     * Get one by id
     * @param id
     * @return
     */
    @Override
    public Flowable<Optional<T>> getById(int id) {
        return getOne(new Filter("id", id), null);
    }

    /**
     * Insert one object
     * @return object inserted
     */
    @Override
    public Flowable<Optional<T>> insertOrUpdate(T object) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        T inserted = realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        T copy = realm.copyFromRealm(inserted);
        realm.close();

        return Flowable.just(Optional.wrap(copy));
    }

    /**
     * Insert or update all
     * @param items
     * @return List of item copied from realm
     */
    @Override
    public Flowable<Optional<List<T>>> insertOrUpdate(List<T> items) {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        items = realm.copyToRealmOrUpdate(items);
        realm.commitTransaction();
        List<T> copies = realm.copyFromRealm(items);
        realm.close();

        return Flowable.just(Optional.wrap(copies));
    }

    @Override
    public Flowable<Optional<T>> insert(T item) {
        return insertOrUpdate(item);
    }

    @Override
    public Flowable<Optional<List<T>>> insert(List<T> items) {
        return insertOrUpdate(items);
    }

    @Override
    public Flowable<Optional<T>> update(final T item) {
        return getOne(item)
                .flatMap(new Function<Optional<T>, Flowable<Optional<T>>>() {
                    @Override
                    public Flowable<Optional<T>> apply(Optional<T> foundOrNull) throws Exception {
                        if(foundOrNull.isNull()){
                            return Flowable.error(new IllegalArgumentException("This item does not exist, you can't update it. You can use insertOrUpdate instead !"));
                        }
                        return insertOrUpdate(item);
                    }
                });
    }

    @Override
    public Flowable<Optional<List<T>>> update(final List<T> items) {
        return getAll(items)
                .flatMap(new Function<Optional<List<T>>, Flowable<Optional<List<T>>>>() {
                    @Override
                    public Flowable<Optional<List<T>>> apply(Optional<List<T>> foundOrNull) throws Exception {
                        if(foundOrNull.isNull() || foundOrNull.get().size() < items.size()){
                            return Flowable.error(new IllegalArgumentException("One or more items do not exist, you can't update them. You can use insertOrUpdate instead !"));
                        }
                        return insertOrUpdate(items);
                    }
                });
    }

    /**
     * Remove only these items
     * @param items
     * @return List of item copied from realm
     */
    @Override
    public Flowable<Integer> delete(List<T> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        for(T obj : items) {
            if(obj.isManaged()) {
                obj.deleteFromRealm(); // potentially slow
            } else {
                T managedObject = realm.copyToRealmOrUpdate(obj);
                managedObject.deleteFromRealm();
            }
        }

        realm.commitTransaction();
        realm.close();

        return Flowable.just(items.size());
    }

    @Override
    public Flowable<Integer> delete(T object) {
        if(object.isManaged()) {
            object.deleteFromRealm();
        } else {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            T managedObject = realm.copyToRealmOrUpdate(object);
            managedObject.deleteFromRealm();
            realm.commitTransaction();
            realm.close();
        }
        return Flowable.just(1);
    }

    @Override
    public Flowable<Integer> deleteAll() {
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<T> query = realm.where(clazz);
        long count = query.count();
        realm.delete(clazz);
        realm.commitTransaction();
        realm.close();

        return Flowable.just((int)count);
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
        return st == null || st == SortType.ASCENDING ? Sort.ASCENDING : Sort.DESCENDING;
    }

    private RealmResults<T> applySortingMode(SortingMode sm, RealmQuery<T> query){
        KeysAndSorts keysAndSorts = convertSortingMode(sm);
        if(keysAndSorts != null){
            return query.findAllSortedAsync(keysAndSorts.key, keysAndSorts.sort);
        }
        return query.findAll();
    }

    /**
     * Convert a Store2Store SortingMode into a class object of Sort[] with String[]
     */
    private KeysAndSorts convertSortingMode(SortingMode sm){
        if(sm == null || sm.entries == null || sm.entries.size() == 0){
            return null;
        }

        final int size = sm.entries.size();
        String[] keys = new String[size];
        Sort[] sorts = new Sort[size];

        int i = 0;
        for(Map.Entry<String, SortType> item: sm.entries){
            keys[i] = item.getKey();
            sorts[i] = convertToSort(item.getValue());
            i++;
        }

        return new KeysAndSorts(keys, sorts);
    }

    /**
     * Key and Sort container
     */
    static private class KeysAndSorts {
        private final String[] key;
        private final Sort[] sort;

        public KeysAndSorts(String[] key, Sort[] sort) {
            this.key = key;
            this.sort = sort;
        }
    }
}
