package com.playmoweb.store2realm.example.data.services;

import com.playmoweb.store2realm.example.data.ApiService;
import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.service.BaseRealmService;
import com.playmoweb.store2store.store.Optional;
import com.playmoweb.store2store.store.StoreDao;
import com.playmoweb.store2store.store.StoreService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
        public Flowable<Optional<List<Post>>> getAll(Filter filter, final SortingMode sortingMode) {

            // this IF case is here only to demonstrate the usage of filtering and sorting mode in the UI
            // this logic should be on the server side and not here !
            // !!!! The filter and the sort are hardcoded here (to match presenter choices).
            if(sortingMode != null && filter != null){
                final int userIdAllowed = (int) filter.entrySet().iterator().next().getValue().value;

                // special return for demo
                return wrapOptional(apiService.getPosts()
                        .flatMapIterable(new Function<List<Post>, Iterable<Post>>() {
                            @Override
                            public Iterable<Post> apply(List<Post> posts) throws Exception {
                                Collections.sort(posts, new Comparator<Post>() {
                                    @Override
                                    public int compare(Post p0, Post p1) {
                                        return p0.userId - p1.userId; // hardcoded ordering by userId
                                    }
                                });
                                return posts;
                            }
                        })
                        .filter(new Predicate<Post>() {
                            @Override
                            public boolean test(Post post) throws Exception {
                                return post.userId == userIdAllowed;
                            }
                        })
                        .toList()
                        .toFlowable()
                );
            }

            // you can wrap the retrofit response directly in a
            // Optional object by default for more convenience
            return wrapOptional(apiService.getPosts());
        }

        // other methods are not implemented because we don't
        // need them in this example and it's perfectly fine
    }
}
