package com.playmoweb.store2realm.example.data.services;

import com.playmoweb.store2realm.example.data.ApiService;
import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.service.BaseRealmService;
import com.playmoweb.store2store.store.Optional;
import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.store.StoreService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    10/09/2017
 */
public class PostService extends StoreService<Post> {

    @Inject
    public PostService(ApiService apiService) {
        super(Post.class, new PostDao(apiService));
        this.syncWith(new BaseRealmService<>(Post.class));
    }

    /**
     * Post DAO impl
     */
    public static class PostDao extends StoreDao<Post> {
        private final ApiService apiService;

        public PostDao(ApiService apiService) {
            this.apiService = apiService;
        }

        @Override
        public Flowable<Optional<List<Post>>> getAll(Filter filter, SortingMode sortingMode) {
            // you can wrap the retrofit response directly in a
            // Optional object by default for more convenience
            return wrapOptional(apiService.getPosts());
        }

        // other methods are not implemented because we don't
        // need them in this example and it's perfectly fine
    }
}
